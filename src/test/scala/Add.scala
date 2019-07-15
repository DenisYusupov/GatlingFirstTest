import io.gatling.core.Predef._
import io.gatling.http.Predef._
// Add item

object Add {

  val add = exec(http("<<_computers")
    .get("/computers")
    .check(status is 200)
    .resources(
      http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
      http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
    ))
    .pause(7)
    .exec(http("<<_computers_new")
      .get("/computers/new")
      .check(status is 200)
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
    .pause(2)
    .exec(http(">>_computers")
      .post("/computers")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .check(status is 200)
      .formParamMap(
        Map(
          "name" -> 	"someComputerName",
          "introduced" -> 	"2016-01-01",
          "discontinued	"	 -> "",
          "company" -> "39"
        )
      )
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
    .exec(http("<<_computers")
      .get("/computers")
      .check(status is 200)
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
}
