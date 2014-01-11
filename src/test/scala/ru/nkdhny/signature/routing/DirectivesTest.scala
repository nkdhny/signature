package ru.nkdhny.signature.routing

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import ru.nkdhny.signature.auth.WithStubbingAuth
import spray.routing._

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 11:07 PM
 */

object directivesWithStubbingAuth extends ValidSessionDirective with WithStubbingAuth

class DirectivesTest extends Specification with Specs2RouteTest with HttpService {

  import directivesWithStubbingAuth._

  def actorRefFactory = system

  val testRoute = path("") {
    userFromValidSession{ user =>
      get {
        complete {
          user._2.id.toString
        }
      }
    }
  }

  Get("/?sessionid=somesession") ~> testRoute ~> check( responseAs[String] === "a.golomedov")
  Get("/?someotherparameter=somesession") ~> testRoute ~> check( handled must beFalse )
  Get("/?sessionid=somenotvalisession") ~> testRoute ~> check( handled must beFalse )

}
