package org.vaslabs.live_streaming

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.gilt.gfc.aws.kinesis.akka.{KinesisNonBlockingStreamSource, KinesisStreamConsumerConfig}
import com.gilt.gfc.aws.kinesis.client.KinesisClientEndpoints
import org.vaslabs.consumer.JsonRecordReader
import org.vaslabs.olivander.OrderConsumer.OrderMessage
import org.vaslabs.olivander.{OrderConsumer, OrderHistoryRepo}
import org.vaslabs.olivander.domain.model.Order
import org.vaslabs.olivander.http.{HttpRouter, OlivanderApi, OlivanderRepo}

import scala.concurrent.ExecutionContext


object DynamoDBApp extends App{

  import io.circe.java8.time._
  import io.circe.generic.auto._

  val streamConfig = config.OlivanderStream()

  implicit val reader = new JsonRecordReader[Order]()

  implicit val actorSystem = ActorSystem("OlivanderSystem")

  implicit val materializer: Materializer = ActorMaterializer()(actorSystem)

  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val kConfig = KinesisStreamConsumerConfig[Either[io.circe.Error, Order]](
    streamConfig.targetStream,
    "olivander",
    kinesisClientEndpoints = Some(KinesisClientEndpoints(streamConfig.dynamoEndpoint, streamConfig.kinesisEndpoint)),
    dynamoCredentialsProvider = streamConfig.aWSCredentials,
    kinesisCredentialsProvider = streamConfig.aWSCredentials,
    initialPositionInStream = InitialPositionInStream.LATEST,
    regionName = Some("us-west-1")
  )

  val orderHistoryRepo: ActorRef = actorSystem.actorOf(OrderHistoryRepo.props(), "orderHistory")

  val orderConsumer: ActorRef = actorSystem.actorOf(OrderConsumer.props(
    orderHistoryRepo, Start, Ack, Done
  ), "orderConsumer")

  val sink = Sink.actorRefWithAck(orderConsumer, Start, Ack, Done)


  val httpRouter = new HttpRouter(new OlivanderApi(orderHistoryRepo))
  val bindingFuture = Http().bindAndHandle(httpRouter.route, "localhost", 8080)

  KinesisNonBlockingStreamSource(kConfig)
  .filter(_.isRight)
  .map(_.right.get)
  .map(OrderMessage)
  .runWith(sink)

}

