package org.vaslabs.olivander.model

import java.time.ZonedDateTime

case class Order(
              userId:Int, orderId:Int, productName:String, aisleName:String, departmentName:String,
               addToCartOrder:Int, reordered:Int,  orderNum:Int,
              orderDow:Int, orderHod:Int, daysSincePrior:Int, dateAdded: ZonedDateTime)
