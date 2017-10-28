package org.vaslabs

import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import com.whisk.docker.impl.spotify.SpotifyDockerFactory
import com.whisk.docker.{DockerContainer, DockerFactory, DockerKit, DockerReadyChecker}


trait DockerService extends DockerKit{


    lazy val awsContainer = DockerContainer("localstack/localstack")
      .withPorts(4568 -> Some(4568), 9000 -> Some(8080))
      .withEnv("SERVICES=kinesis")
      .withReadyChecker(DockerReadyChecker.LogLineContains("Ready."))

  private val client: DockerClient = DefaultDockerClient.fromEnv().build()

  abstract override def dockerContainers: List[DockerContainer] =
      awsContainer :: super.dockerContainers

  override implicit val dockerFactory: DockerFactory = new SpotifyDockerFactory(client)


}
