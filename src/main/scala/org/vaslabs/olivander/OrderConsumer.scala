package org.vaslabs.olivander

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.vaslabs.olivander.domain.model.Order

class OrderConsumer private (orderHistoryRepo: ActorRef, start: Serializable, ack: Serializable, done: Serializable)
  extends Actor with ActorLogging
{
  import OrderConsumer._

  override def receive = {
    case start =>
      sender() ! ack
      context.become(receivingStreamItems())
  }


  private[this] def receivingStreamItems(): Receive = {
    case OrderMessage(order) => {
      orderHistoryRepo ! order
      sender() ! ack
    }
  }

}

object OrderConsumer {
  case class OrderMessage(order: Order)

  def props(orderHistoryRepo: ActorRef, start: Serializable, ack: Serializable, done: Serializable): Props =
    Props(new OrderConsumer(orderHistoryRepo, start, ack, done))
}
