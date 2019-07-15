import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Edit {

  val edit =
    doIf(session => session("computerNumberEdit").as[Int] > 0){ //session => session("computerNumberEdit").as[Int] > 0 //session => session("computerNumberEdit").asOption[String].isDefined
      exec(http("<<_computers/_computerNumber_")
        .get("/computers/${computerNumberEdit}")
        //.check(status.in(200 to 308))
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )
      )
      .pause(2)
      .exec(http(">>_computers/_computerNumber")
        .post("/computers/${computerNumberEdit}")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .check(status is 200)
        .formParamMap(
          Map(
            "name" -> 	"someComputerNameEdit",
            "introduced" -> 	"2015-01-01",
            "discontinued	"	 -> "",
            "company" -> "39"
          )
        )
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )
      )
    }

    .exec(http("<<_computers")
      .get("/computers")
      //.check(status.in(200 to 308))
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
      .pause(2)

}
