package ru.nkdhny.signature.auth

import scala.util.{Success, Try}
import ru.nkdhny.signature.model._

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 9:58 PM
 */

class AuthServiceStub extends AuthService {

  def obtainSession(name: Username, password: Password): Try[Id[Session]] = Success(Id[Session]("someession"))

  def checkSession(sessionId: Id[Session]): Boolean = sessionId == Id[Session]("somesession")

  def userBySessionId(sessionId: Id[Session]): Option[User] = Some(RegisteredUser(Id[RegisteredUser]("a.golomedov")))
}
