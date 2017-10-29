package org.vaslabs

import java.time.ZonedDateTime

import io.circe.Json
import org.vaslabs.olivander.domain.dunnhamby.model.{Order => DunnhumbyOrder, Product => DunnhumbyProduct}
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._
import org.vaslabs.olivander.domain.model.{Order, Product}

package object mapper {

  def map(products: List[DunnhumbyProduct]): List[Product] =
    products.map(dp => {
      Product(dp.productName, dp.aisleName, dp.departmentName, dp.addToCartOrder, dp.reordered)
    })

  def map(order: DunnhumbyOrder): Order = {
    Order(order.userId, order.orderId, order.orderNum, map(order.products), order.orderDow,
      order.orderHod, order.daysSincePrior, ZonedDateTime.now())
  }

  def toJson(order: Order):Json = {
    order.asJson
  }
}
