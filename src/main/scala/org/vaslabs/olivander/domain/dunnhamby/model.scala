package org.vaslabs.olivander.domain.dunnhamby

import org.vaslabs.publisher.PartitionKeyExtractor

object model {

  case class Order(
              userId:Int, orderId:Int, products: List[Product],  orderNum:Int, orderDow:Int, orderHod:Int, daysSincePrior:Int)

  case class Product(
               productName: String, aisleName: String, departmentName: String, addToCartOrder: Int, reordered: Int)


  object Order {
    implicit val partitionKeyExtractor: PartitionKeyExtractor[Order] = _.userId.toString
  }

}
