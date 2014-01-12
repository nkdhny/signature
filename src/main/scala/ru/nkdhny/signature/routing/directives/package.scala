package ru.nkdhny.signature.routing

import spray.routing._
import spray.routing.Directives._
import ru.nkdhny.signature.service.WithConfig
import ru.nkdhny.signature.model.{Session, RegisteredUser, Id}
import ru.nkdhny.signature.auth.{WithMongoDBAuth, AuthService}

/**
 * User: alexey
 * Date: 1/9/14
 * Time: 10:27 PM
 */

trait ValidSessionDirective extends WithConfig {
  protected val authService: AuthService

  private val sessionKey = config.getString("signature.service.session.key")
  private val sessionParameter: Directive1[Id[Session]] = parameter(sessionKey.as[String]).map(Id[Session])

  def userFromValidSession: Directive1[(Id[Session], RegisteredUser)] = sessionParameter.flatMap {

    case sessionId if authService.checkSession(sessionId) => {
      authService.userBySessionId(sessionId) match {
        case Some(u: RegisteredUser) => provide((sessionId, u))
        case _ =>                       reject
      }
    }
    case _ =>                           reject
  }
}

package object directives extends ValidSessionDirective with WithMongoDBAuth {

}
