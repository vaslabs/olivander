package org.vaslabs.olivander

import org.scalatest.FlatSpec
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.java8.time._
import org.vaslabs.mapper._
import org.vaslabs.test_utils


class OrderTest extends FlatSpec{

  "order object" should "be json" in {
    val order = test_utils.dummyOrder
    val jsonExpected = "{\n  \"userId\" : 1,\n  \"orderId\" : 2,\n  \"productName\" : \"product\",\n  \"aisleName\" : \"aisle\",\n  \"departmentName\" : \"department\",\n  \"addToCartOrder\" : 3,\n  \"reordered\" : 4,\n  \"orderNum\" : 5,\n  \"orderDow\" : 6,\n  \"orderHod\" : 7,\n  \"daysSincePrior\" : 8,\n  \"dateAdded\" : \"2017-10-29T02:21:32Z[Europe/London]\"\n}"

    val orderJsonStr = toJson(order).toString()
    println(orderJsonStr)
    assert(orderJsonStr == jsonExpected)
  }

}
