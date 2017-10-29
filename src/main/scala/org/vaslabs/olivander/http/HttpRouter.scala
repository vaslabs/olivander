package org.vaslabs.olivander.http


import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import org.vaslabs.olivander.Query
import sangria.ast.Document
import sangria.execution.Executor
import sangria.parser.QueryParser
import io.circe.syntax._
import sangria.marshalling.circe._
import sangria.renderer.SchemaRenderer

import scala.concurrent.ExecutionContext

class HttpRouter(repo: OlivanderRepo) extends FailFastCirceSupport {



  def route(implicit system: ActorSystem): Route = {
    implicit val executionContext = system.dispatcher
    (post & path("api")) {
      entity(as[Query]) { query =>

        QueryParser.parse(query.query).map(doc => complete(executeQuery(doc))).getOrElse(complete(StatusCodes.BadRequest))

      }
    } ~ (get & path("schema")) {
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, SchemaDefinition.OlivanderProtocolSchema.renderPretty))
    } ~ get {
      getFromResource("graphiql.html")
    }
  }

  def executeQuery(query: Document)(implicit executionContext: ExecutionContext) = {
    Executor.execute(SchemaDefinition.OlivanderProtocolSchema, query, repo)
  }

}
