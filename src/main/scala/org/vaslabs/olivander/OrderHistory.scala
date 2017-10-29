package org.vaslabs.olivander

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.vaslabs.olivander.domain.model.Order

class OrderHistory private() extends Actor with ActorLogging {

  def receiveMoreItems(orders: List[Order]): Receive = {
    case o: Order =>
      context.become(receiveMoreItems(o :: orders))
    case g: OrderHistory.Get =>
      g.replyTo ! orders
  }

  override def receive() = {
    case o: Order =>
      log.info("Stored order {}", o)
      context.become(receiveMoreItems(List(o)))
    case g: OrderHistory.Get => g.replyTo ! List.empty[Order]
  }

}

object OrderHistory {
  def props(): Props = Props(new OrderHistory())

  case class Get(userId: Long, replyTo: ActorRef)

}
