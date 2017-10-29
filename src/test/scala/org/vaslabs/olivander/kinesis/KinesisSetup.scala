package org.vaslabs.olivander.kinesis

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.gilt.gfc.aws.kinesis.client.KinesisPublisher
import org.vaslabs.DockerService

import scala.util.Try

trait KinesisSetup extends DockerService {

  val endpointConfig = new EndpointConfiguration("http://localhost:4568", "us-west-1")

  val kinesisClient = AmazonKinesisClientBuilder.standard()
    .withEndpointConfiguration(
      endpointConfig
    ).build()

  val credentials = new AWSStaticCredentialsProvider(
    new AWSCredentials(){override def getAWSAccessKeyId = "foo"

      override def getAWSSecretKey = "bar"
    })

  val dynamoEndpoint: String = "http://localhost:4569"
  val kinesisEndpoint: String = "http://localhost:4568"

  val streamName = "dunhumby-orders-stream"

  Try {
    kinesisClient.describeStream(streamName)
  }.getOrElse(kinesisClient.createStream(streamName, 1))


  lazy val publisher: KinesisPublisher = KinesisPublisher(
    awsEndpointConfig = Some(endpointConfig),
    awsCredentialsProvider = credentials
  )

}
