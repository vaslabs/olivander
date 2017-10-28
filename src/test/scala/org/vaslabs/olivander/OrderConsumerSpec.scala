package org.vaslabs.olivander

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import org.scalatest.FlatSpecLike
import org.vaslabs.olivander.OrderConsumer.OrderMessage

class OrderConsumerSpec extends TestKit(ActorSystem.create("OlivanderSystem")) with FlatSpecLike{

  case object start
  case object ack

  val streamList: List[Order] =
      (1 to 100).map(Order(_)).toList

  "order consumer streaming data" should "forward to order consumer" in {
    val orderHistoryRepo: ActorRef = system.actorOf(OrderHistoryRepo.props(), "orderHistoryRepo")
    val orderConsumer = system.actorOf(OrderConsumer.props(orderHistoryRepo, start, ack, Done),
    "consumer")

    implicit val sender: ActorRef = this.testActor
    val sink = Sink.actorRefWithAck(orderConsumer, start, ack, Done)

    implicit val materializer: Materializer = ActorMaterializer()

    Source(streamList).map(OrderMessage).runWith(sink)

    Thread.sleep(1000)

    orderHistoryRepo ! OrderHistoryRepo.Get(1)

    expectMsg(List(Order(1)))

    orderHistoryRepo ! OrderHistoryRepo.Get(50)

    expectMsg(List(Order(50)))

    orderHistoryRepo ! OrderHistoryRepo.Get(100)

    expectMsg(List(Order(100)))

  }

}
