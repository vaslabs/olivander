package org.vaslabs.olivander.domain

object model {

  case class DunnhumbyProtocol(userId:Int, orderId:Int, productName:String, aisleName:String, departmentName:String,
                               addToCartOrder:Int, reordered:Int,  orderNum:Int, orderDow:Int, orderHod:Int, daysSincePrior:Int)

}
