package org.vaslabs.consumer

import com.gilt.gfc.aws.kinesis.client.{KCLConfiguration, KCLWorkerRunner, KinesisRecordReader}
import org.scalatest.FlatSpec
import org.vaslabs.olivander.Order

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import io.circe.generic.auto._

class JsonRecordReaderSpec extends FlatSpec {

  "consuming orders" should "give us orders" in {

    implicit val ex: ExecutionContext = ExecutionContext.Implicits.global
    implicit val reader = new JsonRecordReader[Order]()
    KCLWorkerRunner(KCLConfiguration("olivander", "test-stream", maxRecordsPerBatch = None, idleTimeBetweenReads = None))
      .runAsyncSingleRecordProcessor[Either[io.circe.Error, Order]](1 minute) {
      e => Future {
        e.map( e =>  println(e)).left.map(println _)
      }
    }
  }

}
