package org.vaslabs.live_streaming

import java.io.File

import com.gilt.gfc.aws.kinesis.client.KinesisPublisher
import com.github.tototoshi.csv.CSVReader
import org.vaslabs.olivander.domain.dunnhamby.model.Order
import org.vaslabs.publisher.JsonRecordWriter
import io.circe.generic.auto._
import io.circe.java8.time._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.io.Source


object DunhabbyMain extends App{

  val filePath = args.apply(0)

  lazy val kinesisConfig = config.DunhubbyStream()

  def toOrder(fields: List[String]): Order =
    Order(
      fields(0).toInt, fields(1).toInt, fields(2), fields(3), fields(4), fields(5).toInt, fields(6).toInt,
      fields(7).toInt, fields(8).toInt, fields(9).toInt, fields(10).toInt
    )

  lazy val publisher: KinesisPublisher = KinesisPublisher(
    awsEndpointConfig = Some(kinesisConfig.kinesisEndpoint),
    awsCredentialsProvider = kinesisConfig.awsCredentials
  )

  implicit val executionContext = ExecutionContext.Implicits.global
  implicit val kinesisWriter = new JsonRecordWriter[Order]()


  CSVReader.open(
    Source.fromFile(new File(filePath))).toStream
    .map(toOrder _)
    .foreach(order => {
      val future = publisher.publishBatch(kinesisConfig.streamName, List(order))
      Await.ready(future, 2 second)
    })


}
