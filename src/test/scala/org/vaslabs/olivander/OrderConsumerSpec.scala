package org.vaslabs.olivander

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import org.scalatest.FlatSpecLike
import org.vaslabs.olivander.OrderConsumer.OrderMessage
import org.vaslabs.olivander.domain.model.Order
import org.vaslabs.test_utils

class OrderConsumerSpec extends TestKit(ActorSystem.create("OlivanderSystem")) with FlatSpecLike{

  case object start
  case object ack

  val streamList: List[Order] =
      (1 to 100).map(id => test_utils.dummyOrder.copy(userId = id)).toList

  "order consumer streaming data" should "forward to order consumer" in {
    val orderHistoryRepo: ActorRef = system.actorOf(OrderHistoryRepo.props(), "orderHistoryRepo")
    val orderConsumer = system.actorOf(OrderConsumer.props(orderHistoryRepo, start, ack, Done),
    "consumer")

    implicit val sender: ActorRef = this.testActor
    val sink = Sink.actorRefWithAck(orderConsumer, start, ack, Done)

    implicit val materializer: Materializer = ActorMaterializer()

    Source(streamList).map(OrderMessage).runWith(sink)

    orderHistoryRepo ! OrderHistoryRepo.Get(1)

    expectMsg(List(test_utils.dummyOrder.copy(userId = 1)))

    orderHistoryRepo ! OrderHistoryRepo.Get(50)

    expectMsg(List(test_utils.dummyOrder.copy(userId = 50)))

    orderHistoryRepo ! OrderHistoryRepo.Get(100)

    expectMsg(List(test_utils.dummyOrder.copy(userId = 100)))

  }

}
