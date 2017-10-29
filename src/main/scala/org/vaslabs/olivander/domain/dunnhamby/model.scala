package org.vaslabs.olivander.domain.dunnhamby

import org.vaslabs.publisher.PartitionKeyExtractor

object model {

  case class Order(
              userId:Int, orderId:Int, productName:String, aisleName:String, departmentName:String,
               addToCartOrder:Int, reordered:Int,  orderNum:Int, orderDow:Int, orderHod:Int, daysSincePrior:Int)

  object Order {
    implicit val partitionKeyExtractor: PartitionKeyExtractor[Order] = _.userId.toString
  }

}
