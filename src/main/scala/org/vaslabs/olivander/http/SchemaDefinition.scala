package org.vaslabs.olivander.http

import org.vaslabs.olivander.domain.model.OlivanderProtocol
import sangria.schema._
import sangria.macros.derive._
object SchemaDefinition {

  val OrderType = deriveObjectType[Unit, OlivanderProtocol](ObjectTypeDescription("The product picture"))

  val Id = Argument("userId", IntType)


  val Query = ObjectType(
    "Olivander",
    "description - Olivander domain",

    fields[OlivanderRepo, Unit](
      Field(
        "order", ListType(OrderType), description = None, arguments = Id :: Nil,
        resolve = c => c.ctx.userOrders(c arg Id)
      )
  ))

  val OlivanderProtocolSchema = Schema(Query)

}
