package org.vaslabs.olivander.http

import org.vaslabs.olivander.domain.model.Order

import scala.concurrent.Future


trait OlivanderRepo {

  def userOrders(userId: Int): Future[List[Order]]

}
