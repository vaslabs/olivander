package org.vaslabs.olivander

import org.vaslabs.publisher.PartitionKeyExtractor


case class Order(userId: Long)

object Order {
  implicit val partitionKeyExtractor: PartitionKeyExtractor[Order] = _.userId.toString
}