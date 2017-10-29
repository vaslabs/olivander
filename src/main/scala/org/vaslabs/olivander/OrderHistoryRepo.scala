package org.vaslabs.olivander

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import org.vaslabs.olivander.domain.model.Order

class OrderHistoryRepo extends Actor with ActorLogging{

  import OrderHistoryRepo._

  val orderHistoryRegion: ActorRef = ClusterSharding(context.system).start(
    typeName = "OrderHistory",
    entityProps = OrderHistory.props(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

  private[this] def dispatchToUserHistory(msg: Any): Unit = {
    orderHistoryRegion ! msg
  }

  override def receive = {
    case o: Order => dispatchToUserHistory(o)
    case g: Get =>
      val senderRef = sender()
      dispatchToUserHistory(OrderHistory.Get(g.userId, senderRef))
  }

  private[this] def extractEntityId: ShardRegion.ExtractEntityId = {
    case msg @ Order(userId, _, _, _, _, _, _, _) =>
      (userId.toString, msg)
    case msg @ OrderHistory.Get(userId, replyTo) =>
      (userId.toString, msg)
  }

  private[this] val numberOfShards = 100

  private[this] def extractShardId: ShardRegion.ExtractShardId = {
    case Order(userId, _, _, _,_,_, _, _) ⇒ (userId % numberOfShards).toString
    case OrderHistory.Get(userId, replyTo) => (userId % numberOfShards).toString
    case ShardRegion.StartEntity(id) ⇒
      (id.toLong % numberOfShards).toString
  }
}

object OrderHistoryRepo {
  def props(): Props = Props(new OrderHistoryRepo())

  case class Get(userId: Int)
}
