package org.vaslabs.live_streaming

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.gilt.gfc.aws.kinesis.akka.{KinesisNonBlockingStreamSource, KinesisStreamConsumerConfig}
import com.gilt.gfc.aws.kinesis.client.{KinesisClientEndpoints, KinesisPublisher, KinesisRecordWriter}
import org.vaslabs.consumer.JsonRecordReader
import org.vaslabs.live_streaming.DunhabbyMain.kinesisConfig
import org.vaslabs.mapper
import org.vaslabs.olivander.domain.model.Order
import org.vaslabs.olivander.domain.dunnhamby.model.{Order => DunnhambyOrder}

import org.vaslabs.publisher.JsonRecordWriter

import scala.concurrent.ExecutionContext

case object Start
case object Ack

object OlivanderMain extends App {

  import io.circe.java8.time._
  import io.circe.generic.auto._

  val streamConfig = config.OlivanderStream()

  implicit val reader = new JsonRecordReader[DunnhambyOrder]()

  implicit val actorSystem = ActorSystem("OlivanderStreamer")

  implicit val materializer: Materializer = ActorMaterializer()(actorSystem)

  implicit val kinesisRecordWriter: KinesisRecordWriter[Order] = new JsonRecordWriter[Order]()

  val kConfig = KinesisStreamConsumerConfig[Either[io.circe.Error, DunnhambyOrder]](
    streamConfig.sourceStream,
    "olivander",
    kinesisClientEndpoints = Some(KinesisClientEndpoints(streamConfig.dynamoEndpoint, streamConfig.kinesisEndpoint)),
    dynamoCredentialsProvider = streamConfig.aWSCredentials,
    kinesisCredentialsProvider = streamConfig.aWSCredentials,
    initialPositionInStream = InitialPositionInStream.LATEST,
    regionName = Some("us-west-1")
  )

  lazy val publisher: KinesisPublisher = KinesisPublisher(
    awsEndpointConfig = Some(kinesisConfig.kinesisEndpoint),
    awsCredentialsProvider = kinesisConfig.awsCredentials
  )

  implicit val executionContext = actorSystem.dispatcher

  val actorRef = actorSystem.actorOf(OlivanderOrderPublisher.props(streamConfig.targetStream, publisher), "orderPublisher")

  val sink = Sink.actorRefWithAck(actorRef, Start, Ack, Done)

  KinesisNonBlockingStreamSource(kConfig)
      .filter(_.isRight)
        .map(_.right.get)
        .map(mapper.map)
        .grouped(10)
        .map(_.toList)
        .map(OrderBatch)
        .runWith(sink)
}


class OlivanderOrderPublisher private (streamName: String, publisher: KinesisPublisher)(implicit kinesisRecordWriter: KinesisRecordWriter[Order], executionContext: ExecutionContext) extends Actor with ActorLogging {
  override def receive = {
    case Start =>
      log.info("Starting with {}", Start)
      sender() ! Ack
    case OrderBatch(orders) =>
      val senderRef = sender()
      publisher.publishBatch(streamName, orders).foreach(r => {
        log.info("Written: {}", r.successRecordCount)
        senderRef ! Ack
      })
    case Done => self ! PoisonPill
  }
}

case class OrderBatch(orders: List[Order])

object OlivanderOrderPublisher {
  def props(streamName: String, publisher: KinesisPublisher)
           (implicit kinesisRecordWriter: KinesisRecordWriter[Order], executionContext: ExecutionContext) =
    Props(new OlivanderOrderPublisher(streamName, publisher))

}