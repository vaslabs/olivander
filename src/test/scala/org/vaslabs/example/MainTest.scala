package org.vaslabs.example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.scalatest.{Matchers, WordSpec}
import io.circe.generic.auto._
import org.vaslabs.olivander.Query
import org.vaslabs.olivander.http.HttpRouter

class MainTest extends WordSpec with Matchers with ScalatestRouteTest with FailFastCirceSupport {



  "The service" should {

    "return a greeting for POST requests to the root path" in {
      val router = new HttpRouter {}

      // tests:
      Post("/hello", Query("jshdfgsjhgfj", "zzzzzzzzzzzz")) ~> router.route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

}
