package org.vaslabs.live_streaming

import java.io.File

import com.gilt.gfc.aws.kinesis.client.KinesisPublisher
import org.vaslabs.olivander.domain.dunnhamby.model.Order
import org.vaslabs.publisher.JsonRecordWriter
import io.circe.generic.auto._
import io.circe.java8.time._
import io.circe.parser._
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.io.Source


object DunhabbyMain extends App{

  val filePath = args.apply(0)

  lazy val kinesisConfig = config.DunhubbyStream()


  lazy val publisher: KinesisPublisher = KinesisPublisher(
    awsEndpointConfig = Some(kinesisConfig.kinesisEndpoint),
    awsCredentialsProvider = kinesisConfig.awsCredentials
  )

  implicit val executionContext = ExecutionContext.Implicits.global
  implicit val kinesisWriter = new JsonRecordWriter[Order]()


    Source.fromFile(new File(filePath)).getLines()
    .map(
      parse(_).flatMap(_.as[Order])
    ).filter(_.isRight)
        .map(_.right.get)
    .foreach(order => {
      val future = publisher.publishBatch(kinesisConfig.streamName, List(order))
      Await.ready(future, 2 second)
    })


}
