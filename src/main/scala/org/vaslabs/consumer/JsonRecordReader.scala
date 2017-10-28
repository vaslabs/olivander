package org.vaslabs.consumer

import com.amazonaws.services.kinesis.model.Record
import com.gilt.gfc.aws.kinesis.client.KinesisRecordReader
import io.circe.{Decoder}
import io.circe.parser._


class JsonRecordReader[A](implicit decoder: Decoder[A]) extends KinesisRecordReader[Either[io.circe.Error, A]]{
  override def apply(r: Record) = parse(new String(r.getData.array(), "UTF-8")).flatMap(_.as[A])
}
