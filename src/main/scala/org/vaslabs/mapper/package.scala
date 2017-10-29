package org.vaslabs

import java.time.ZonedDateTime

import io.circe.Json
import org.vaslabs.olivander.domain.dunnhamby.model.{Order => DunnhumbyOrder}
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._
import org.vaslabs.olivander.domain.model.Order

package object mapper {

  def map(order: DunnhumbyOrder): Order = {
    Order(order.userId, order.orderId, order.productName,
      order.aisleName, order.departmentName, order.addToCartOrder,
      order.reordered, order.orderNum, order.orderDow,
      order.orderHod, order.daysSincePrior, ZonedDateTime.now())
  }

  def toJson(order: Order):Json = {
    order.asJson
  }
}
