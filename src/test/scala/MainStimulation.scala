package org.vaslabs.metrics

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MainStimulation extends Simulation {

  val httpConf = http
    .baseURL("http://google.co.uk")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("BasicSimulation")
    .exec(http("test")
    .get("/"))
    .pause(5)

  setUp( // 11
    scn.inject(atOnceUsers(10))
  ).protocols(httpConf)
}
