package org.vaslabs.metrics

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class MainStimulation extends Simulation {

  val request =
    """
       {
 	      "query": "{order(userId: $$userId$$) {dateAdded products {productName departmentName} orderNum userId orderHod } }"
       }
     """

  val userIds =  {
    val users = (120006 to 140001).toVector
    Iterator.continually {
      val rndIdx = Random.nextInt(users.size)
      val userId = users(rndIdx)
      Map("userId" -> userId)
    }
  }

  private[this] lazy val requestPayload = request

  private[this] def templateRequest(userId: Expression[String]) = {
    StringBody {
      session =>
        userId(session)
          .map(requestPayload.replace("$$userId$$", _))
    }
  }

  protected def searchRequest = {
    http("olivander")
      .post("/api")
      .header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson)
      .body(templateRequest(f"$${userId}"))
      .check(status is 200)
  }

  val httpConf = http
    .baseURL("http://localhost:8080")
    .acceptHeader("application/json")
    .userAgentHeader("olivander/gatling")

  val rampUpSearchLimit = repeat(50) {
    feed(userIds).exec(searchRequest).pause(500 milliseconds)
  }

  val rampUpUsers = scenario("Ramp up concurrent users")
    .exec(rampUpSearchLimit)
    .inject(rampUsersPerSec(10).to(50).during(15 seconds))

  val concurrentUsers = scenario("Users at once")
    .exec(pause(1 seconds).exec(rampUpSearchLimit))
    .inject(atOnceUsers(25))

  setUp(
    rampUpUsers
  ).protocols(httpConf)

}
