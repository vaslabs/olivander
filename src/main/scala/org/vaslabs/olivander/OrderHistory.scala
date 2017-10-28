package org.vaslabs.olivander

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}

class OrderHistory private() extends Actor with ActorLogging with Stash{

  def receiveMoreItems(orders: List[Order]): Receive = {
    case o: Order =>
      context.become(receiveMoreItems(o :: orders))
    case g: OrderHistory.Get =>
      g.replyTo ! orders
  }

  override def receive() = {
    case o: Order =>
      context.become(receiveMoreItems(List(o)))
      unstashAll()
    case g: OrderHistory.Get => stash()
  }

}

object OrderHistory {
  def props(): Props = Props(new OrderHistory())

  case class Get(userId: Long, replyTo: ActorRef)

}
