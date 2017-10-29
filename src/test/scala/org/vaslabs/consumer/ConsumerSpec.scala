package org.vaslabs.consumer

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.amazonaws.services.kinesis.model.Record
import com.gilt.gfc.aws.kinesis.akka.{KinesisNonBlockingStreamSource, KinesisStreamConsumerConfig, KinesisStreamSource}
import com.gilt.gfc.aws.kinesis.client.{KinesisClientEndpoints, KinesisRecordReader}
import io.circe.generic.auto._
import org.scalatest.{AsyncFlatSpecLike, FlatSpecLike}
import org.vaslabs.olivander.Order
import org.vaslabs.olivander.kinesis.KinesisSetup
import org.vaslabs.publisher.JsonRecordWriter

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case object start
case object ack
class ConsumerSpec extends TestKit(ActorSystem("Olivander")) with FlatSpecLike with KinesisSetup {


  implicit val ex: ExecutionContext = ExecutionContext.Implicits.global
  implicit val jsonRecordWriter = new JsonRecordWriter[Order]()



  "consuming orders" should "give us orders" in {


    implicit val reader = new JsonRecordReader[Order]()

    implicit val materializer: Materializer = ActorMaterializer()(system)

    val config = KinesisStreamConsumerConfig[Either[io.circe.Error, Order]](
      streamName,
      "olivander",
      kinesisClientEndpoints = Some(KinesisClientEndpoints(dynamoEndpoint, kinesisEndpoint)),
      dynamoCredentialsProvider = credentials,
      kinesisCredentialsProvider = credentials,
      initialPositionInStream = InitialPositionInStream.LATEST,
      checkPointInterval = 1 second,
      regionName = Some("us-west-1")
    )

    val testProbe = TestProbe()

    val sink = Sink.actorRefWithAck(testProbe.ref, start, ack, Done)


    val streamSource = KinesisNonBlockingStreamSource(config)



    streamSource.filter(_.isRight).map(_.right.get).runWith(sink)


    Future {
      publisher.publishBatch(streamName, List(
        Order(1),
        Order(2),
        Order(3),
      ))
    }

    Thread.sleep(30000)

    testProbe.expectMsg(start)
    testProbe.reply(ack)
    testProbe.expectMsg(Order(1))
    testProbe.reply(ack)
    testProbe.expectMsg(Order(2))
    testProbe.reply(ack)
    testProbe.expectMsg(Order(3))
    testProbe.reply(ack)
  }

}
