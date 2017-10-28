package org.vaslabs.example

import io.circe.{Decoder, Encoder}


case class Query(body: String, variables: String)

object Query {
  import io.circe.generic.semiauto._
  implicit val encoder: Encoder[Query] = deriveEncoder[Query]
  implicit val decoder: Decoder[Query] = deriveDecoder[Query]
}
