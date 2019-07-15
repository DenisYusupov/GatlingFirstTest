import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._



class Scn_1 extends Simulation {
  val csvSearchData = csv("searchData.csv").circular


  object Search {
    val searchComputerName = "ACE"
    val model = ""
    val searchRandom = exec(http("<<_/")
      .get("/")
      .check(status is 200, regex("<td><a href=\"(.+?)\"").saveAs("searchComputerName"))
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )
      )
      .pause(2)
      .exec(http("<<_computers")
        .get("/computers")
        .queryParam("f", "${searchComputerName}")  //"someComputerName"
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )

      )


    val searchFromFile = feed(csvSearchData).exec(http("<<_/")
      .get("/")
      .check(status is 200))
      .pause(2)
      .exec(http("<<_computers_model")
        .get("/computers") //?f=${model}
        .queryParam("f", "${model}")
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        )
      )

    val search = exec(http("<<_/")
      .get("/")
      //.check(status.in(200 to 308))
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
      .pause(2)

      .exec(http("<<_computers")
        .get("/computers")
        //.check(status.in(200 to 308))
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        ))
      .pause(2)

      .exec(http("<<_computers")
        .get("/computers")
        .queryParam("f", "someComputerName")  //${editComputerName}
        .check( checkIf((r: Response, s: Session) => r.body.string.contains("computers found"))
            (regex("<td><a href=\"\\/computers\\/(.+?)\"").saveAs("computerNumber"))
            )
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        ))
      .pause(7)

    val searchEdit = exec(http("<<_/")
      .get("/")
      //.check(status.in(200 to 308))
      .resources(
        http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
        http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
      ))
      .pause(2)

      .exec(http("<<_computers")
        .get("/computers")
        //.check(status.in(200 to 308))
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        ))
      .pause(2)

      .exec(http("<<_computers")
        .get("/computers")
        .queryParam("f", "someComputerNameEdit")  //${editComputerName}
        .check( checkIf((r: Response, s: Session) => r.body.string.contains("computers found"))
      (regex("<td><a href=\"\\/computers\\/(.+?)\"").saveAs("computerNumberEdit")))
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        ))
      .pause(7)

  }



  object Browse {
    val browse = repeat(4, "i") {
      exec(http("/computers?_page_")
        .get("/computers?p=${i}")
        .resources(
          http("bootstrap.min.css").get("http://computer-database.gatling.io/assets/stylesheets/bootstrap.min.css"),
          http("main.css").get("http://computer-database.gatling.io/assets/stylesheets/main.css")
        ))
        .pause(1)
    }
  }


 val httpProtocol = http
   .baseUrl("http://computer-database.gatling.io")
   .acceptEncodingHeader("gzip, deflate")
   .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
   .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
   .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
   .upgradeInsecureRequestsHeader("1")
   .check(status.in(200 to 308))
   .disableCaching
   //.extraInfoExtractor()

    val computerNumberEdit = "2594"
    val computerNumber ="0"

    val search = scenario("search").forever(exec(Search.search,Add.add))
    val del = scenario("delete").forever(exec(Search.search,Delete.delete)) //
    val edit = scenario("edit").forever(exec(Search.searchEdit,Edit.edit))  //
    val browse = scenario("browse").forever(randomSwitch(
      (25,Browse.browse ),
      (15, Search.searchRandom),
      (60, Search.searchFromFile)
    ))
    val tmp = scenario("tmp").exec(Search.searchFromFile)


    var maxDuration = 60

    //splitUsers(nbUsers) into(injectionStep) separatedBy(duration)
    val injectionStepsNormalSearch = rampUsers(40) during (5 minutes)
    val injectionStepsNormalEditDel = rampUsers(20) during (5 minutes)

    val injectionStepsMaxSearch = constantUsersPerSec(0.5) during(maxDuration minutes)
    val injectionStepsMaxEditDel = constantUsersPerSec(0.5) during(maxDuration minutes)

    val injectionStepsStabSearch = rampUsers(100) during (10 minutes)
    val injectionStepsStabEditDel= rampUsers(60) during (10 minutes)
    // maxDuration = 360

    val currInjectSearch = injectionStepsNormalSearch //injectionStepsMaxSearch //injectionStepsStabSearch
    val currInjectEditDel = injectionStepsNormalEditDel //injectionStepsMaxEditDel //injectionStepsStabEditDel

  //  val currInjectSearch = injectionStepsMaxSearch //injectionStepsStabSearch
  //  val currInjectEditDel = injectionStepsMaxEditDel //injectionStepsStabEditDel

    setUp(
    search.inject(currInjectSearch),
    del.inject(currInjectEditDel),
    edit.inject(currInjectEditDel),
    browse.inject(currInjectSearch)

  ).protocols(httpProtocol)
    .assertions(Seq(
      global.responseTime.percentile3.lte(550),
      global.failedRequests.percent.lte(5),
      details(">>_computers").responseTime.percentile3.lte(300)
    ))
    .maxDuration(maxDuration minutes)

}

