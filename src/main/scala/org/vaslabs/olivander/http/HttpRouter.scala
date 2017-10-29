package org.vaslabs.olivander.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import org.vaslabs.olivander.Query
import org.vaslabs.olivander.domain.model
import org.vaslabs.olivander.domain.model.OlivanderProtocol
import sangria.ast.Document
import sangria.execution.Executor
import sangria.parser.QueryParser
import io.circe.syntax._
import sangria.marshalling.circe._


import scala.concurrent.ExecutionContext

trait HttpRouter extends FailFastCirceSupport {

  def route(implicit system: ActorSystem): Route = {
    implicit val executionContext = system.dispatcher
    (post & path("api")) {
      entity(as[Query]) { query =>

        QueryParser.parse(query.query).map(doc => complete(executeQuery(doc))).getOrElse(complete(StatusCodes.BadRequest))

      }
    }
  }

  def executeQuery(query: Document)(implicit executionContext: ExecutionContext) = {
    Executor.execute(SchemaDefinition.OlivanderProtocolSchema, query, new OlivanderRepo {
      override def userOrders(userId: Int): List[model.OlivanderProtocol] =
        List(OlivanderProtocol(userId, 1, "pn", "an", "dn", 1, 2, 1, 1, 1, 1))
    })
  }

}
