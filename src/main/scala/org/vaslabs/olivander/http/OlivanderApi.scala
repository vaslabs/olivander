package org.vaslabs.olivander.http

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import org.vaslabs.olivander.OrderHistoryRepo
import org.vaslabs.olivander.domain.model.Order
import scala.concurrent.duration._
class OlivanderApi(orderHistoryRepo: ActorRef) extends OlivanderRepo{

  implicit val timeout: Timeout = Timeout(2 seconds)
  override def userOrders(userId: Int) =
    (orderHistoryRepo ? OrderHistoryRepo.Get(userId)).mapTo[List[Order]]
}
