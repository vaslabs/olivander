package org.vaslabs.live_streaming

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder

object config {

  val endpointConfig = new EndpointConfiguration("http://localhost:4568", "us-west-1")

  val kinesisClient = AmazonKinesisClientBuilder.standard()
    .withEndpointConfiguration(
      endpointConfig
    ).build()

  val credentials = new AWSStaticCredentialsProvider(
    new AWSCredentials(){override def getAWSAccessKeyId = "foo"

      override def getAWSSecretKey = "bar"
    })

  val kinesisEndpoint: String = "http://localhost:4568"

  val streamName = "dunhumby-orders-stream"

  val dynamoEndpoint: String = "http://localhost:4569"


  case class DunhubbyStream(streamName: String = streamName,
                            kinesisEndpoint: EndpointConfiguration = endpointConfig,
                            awsCredentials: AWSCredentialsProvider = credentials)

  case class OlivanderStream(streamName: String = "olivander-order-stream",
                             kinesisEndpoint: String = kinesisEndpoint,
                             aWSCredentials: AWSCredentialsProvider = credentials,
                             dynamoEndpoint: String = dynamoEndpoint)
}
