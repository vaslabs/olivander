package org.vaslabs

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

import org.vaslabs.olivander.domain.model.Order

object test_utils {
  def dummyOrder: Order = {
    Order(1, 2, "product", "aisle", "department",
      3,4,  5, 6, 7, 8, ZonedDateTime.of(LocalDateTime.of(2017, 10, 29, 2, 21, 32), ZoneId.of("Europe/London")))
  }

}
