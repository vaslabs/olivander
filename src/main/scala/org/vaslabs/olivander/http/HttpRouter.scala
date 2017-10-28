package org.vaslabs.olivander.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.vaslabs.olivander.Query
import io.circe.generic.auto._

trait HttpRouter extends FailFastCirceSupport
{

  def route: Route =
    (post & path("hello")) {
      entity(as[Query]) {
        query => {
          complete(StatusCodes.OK)
        }
      }
    }
}
