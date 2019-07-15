import io.gatling.core.Predef._
import io.gatling.http.Predef._


object Delete {
  val delete =
    doIf(session => session("computerNumberEdit").as[Int] > 0){ //session => session("computerNumber").asOption[String].isDefined
      exec(http("<<_computers/_computerNumber_")
        .get("/computers/${computerNumber}")
        //.check(status.in(200 to 308))
        )
      .pause(7)
      .exec(http("<<_computers/_computerNumber_/delete")
        .post("/computers/${computerNumber}/delete")
        .header("Content-Type", "application/x-www-form-urlencoded")
      .check(status is 200)
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )
        )
        .exec(session =>session.set("computerNumber", "0"))
    }

    .exec(http("<<_computers")
      .get("/computers")
      //.check(status.in(200 to 308))
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      )
    )
      .pause(2)

}
