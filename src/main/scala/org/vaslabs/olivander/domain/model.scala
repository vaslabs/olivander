package org.vaslabs.olivander.domain

import java.time.ZonedDateTime

import org.vaslabs.publisher.PartitionKeyExtractor

object model {

  case class Order(userId:Int, orderId:Int,  orderNum:Int, products: List[Product], orderDow:Int, orderHod:Int, daysSincePrior:Int, dateAdded: ZonedDateTime)

  case class Product(productName:String, aisleName:String, departmentName:String,
                     addToCartOrder:Int, reordered:Int)

  object Order {
    implicit val partitionKeyExtractor: PartitionKeyExtractor[Order] = _.userId.toString
  }

}
