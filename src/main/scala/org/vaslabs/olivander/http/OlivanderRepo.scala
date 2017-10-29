package org.vaslabs.olivander.http

import org.vaslabs.olivander.domain.model.OlivanderProtocol


trait OlivanderRepo {

  def userOrders(userId: Int): List[OlivanderProtocol]

}
