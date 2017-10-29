package org.vaslabs.olivander.http

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.vaslabs.olivander.domain.model.Order
import sangria.macros.derive._
import sangria.schema._

object implicits {
  final val dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
  implicit val zonedDateTimeScalar = ScalarAlias[ZonedDateTime, String](
    StringType, _.format(dateTimeFormatter), dateString => Right(ZonedDateTime.parse(dateString, dateTimeFormatter))
  )
}

object SchemaDefinition {

  import implicits._
  val OrderType = deriveObjectType[Unit, Order](ObjectTypeDescription("The product picture"))

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
