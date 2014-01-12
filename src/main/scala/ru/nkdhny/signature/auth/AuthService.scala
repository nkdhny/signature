package ru.nkdhny.signature.auth

import scala.util.{Success, Try}
import ru.nkdhny.signature.model.{User, Session, Id}

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 9:52 PM
 */
trait AuthService {

  type Username = String
  type Password = String

  def obtainSession(name: Username, password: Password): Try[Id[Session]]
  def checkSession(sessionId: Id[Session]): Boolean
  def userBySessionId(sessionId: Id[Session]): Option[User]

}

object AuthService {
  class WrongLoginOrPasswordException extends Exception
}


