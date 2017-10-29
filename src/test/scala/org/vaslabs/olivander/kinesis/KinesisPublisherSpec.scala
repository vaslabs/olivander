package org.vaslabs.olivander.kinesis

import org.scalatest.AsyncFlatSpecLike
import org.vaslabs.olivander.Order
import org.vaslabs.publisher.JsonRecordWriter

import scala.concurrent.ExecutionContext


class KinesisPublisherSpec extends AsyncFlatSpecLike with KinesisSetup{

  implicit val exC: ExecutionContext = ExecutionContext.Implicits.global



  "when firing something in the stream" should "be able to read it back" in {
    import io.circe.generic.auto._
    implicit val writer = new JsonRecordWriter[Order]()

    publisher.publishBatch("dunhumby-orders-stream", List(Order(1)))
        .map(
          r => assert(r.successRecordCount == 1)
        )
  }


}
