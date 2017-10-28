package org.vaslabs.example

import com.gilt.gfc.aws.kinesis.client.{KinesisRecord, KinesisRecordWriter}
import io.circe.{Encoder}
import io.circe.syntax._
import example._

class JsonRecordWriter[A](implicit encoder: Encoder[A], partitionKeyExtractor: PartitionKeyExtractor[A]) extends KinesisRecordWriter[A] {

    override def toKinesisRecord(a: A) : KinesisRecord = {
      KinesisRecord(a.partitionKey, a.asJson.noSpaces.getBytes("UTF-8"))
    }
}
