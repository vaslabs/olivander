package org.vaslabs.live_streaming

import akka.stream.{ActorMaterializer, Materializer}
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream
import com.gilt.gfc.aws.kinesis.akka.{KinesisNonBlockingStreamSource, KinesisStreamConsumerConfig}
import com.gilt.gfc.aws.kinesis.client.KinesisClientEndpoints
import org.vaslabs.consumer.JsonRecordReader
import org.vaslabs.olivander.Order

case object start
case object ack

object OlivanderMain extends App {

  val streamConfig = config.OlivanderStream()

  implicit val reader = new JsonRecordReader[Order]()

  implicit val materializer: Materializer = ActorMaterializer()(system)

  val kConfig = KinesisStreamConsumerConfig[Either[io.circe.Error, Order]](
    streamConfig.streamName,
    "olivander",
    kinesisClientEndpoints = Some(KinesisClientEndpoints(streamConfig.dynamoEndpoint, streamConfig.kinesisEndpoint)),
    dynamoCredentialsProvider = streamConfig.aWSCredentials,
    kinesisCredentialsProvider = streamConfig.aWSCredentials,
    initialPositionInStream = InitialPositionInStream.LATEST,
    regionName = Some("us-west-1")
  )


  KinesisNonBlockingStreamSource(streamConfig)
      .filter(_.isRight)
        .map(_.right.get)
            .run
}
