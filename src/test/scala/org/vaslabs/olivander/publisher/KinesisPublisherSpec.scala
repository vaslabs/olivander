package org.vaslabs.olivander.publisher

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.gilt.gfc.aws.kinesis.client.KinesisPublisher
import org.scalatest.AsyncFlatSpecLike
import org.vaslabs.DockerService
import org.vaslabs.olivander.Order
import org.vaslabs.publisher.JsonRecordWriter

import scala.concurrent.ExecutionContext
import scala.util.Try


class KinesisPublisherSpec extends DockerService with AsyncFlatSpecLike{

  val endpointConfig = new EndpointConfiguration("http://localhost:4568", "us-west-1")

  implicit val exC: ExecutionContext = ExecutionContext.Implicits.global

  val kinesisClient = AmazonKinesisClientBuilder.standard()
    .withEndpointConfiguration(
      endpointConfig
    ).build()

  Try {
    kinesisClient.describeStream("dunhumby-orders-stream")
  }.getOrElse(kinesisClient.createStream("dunhumby-orders-stream", 1))



  "when firing something in the stream" should "be able to read it back" in {
    import io.circe.generic.auto._
    implicit val writer = new JsonRecordWriter[Order]()

    val publisher: KinesisPublisher = KinesisPublisher(
      awsEndpointConfig = Some(endpointConfig),
      awsCredentialsProvider = new AWSStaticCredentialsProvider(
        new AWSCredentials(){override def getAWSAccessKeyId = "foo"

        override def getAWSSecretKey = "bar"
      })
    )

    publisher.publishBatch("dunhumby-orders-stream", List(Order(1)))
        .map(
          r => assert(r.successRecordCount == 1)
        )
  }


}
