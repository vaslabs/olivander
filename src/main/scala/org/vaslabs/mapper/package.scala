package org.vaslabs

import java.time.ZonedDateTime

import io.circe.Json
import org.vaslabs.olivander.Order
import org.vaslabs.olivander.domain.model
import io.circe.syntax._

import io.circe.generic.auto._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.java8.time._

package object mapper {

  def map(order: model.Order): Order = {
    Order(order.userId, order.orderId, order.productName,
      order.aisleName, order.departmentName, order.addToCartOrder,
      order.reordered, order.orderNum, order.orderDow,
      order.orderHod, order.daysSincePrior, ZonedDateTime.now())
  }

  def toJson(order: Order):Json = {
    order.asJson
  }
}
