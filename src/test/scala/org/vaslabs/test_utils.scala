package org.vaslabs

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import org.vaslabs.olivander.domain.model.{Order, Product}

object test_utils {
  def dummyOrder: Order = {
    Order(1, 2, 3, List(Product("product", "aisle", "department",4,  5)), 6, 7, 8, ZonedDateTime.now())
  }

}
