package ru.nkdhny.signature.routing

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import ru.nkdhny.signature.auth.WithStubbingAuth

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 11:07 PM
 */

object directivesWithStubbingAuth extends ValidSessionDirective with WithStubbingAuth

class DirectivesTest extends Specification with Specs2RouteTest {

  import directivesWithStubbingAuth._

  Get("/?sessionid=somesession") ~> userFromValidSession(u => u._2.id.toString) ~> check( responseAs[String] === "a.golomedov")
  Get("/?someotherparameter=somesession") ~> userFromValidSession(u => u._2.id.toString) ~> check( handled must beFalse )
  Get("/?sessionid=somenotvalisession") ~> userFromValidSession(u => u._2.id.toString) ~> check( handled must beFalse )

}
