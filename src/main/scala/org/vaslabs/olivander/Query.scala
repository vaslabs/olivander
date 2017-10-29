package org.vaslabs.olivander

import io.circe.Json

case class Query(query: String, variables: Option[Json])
