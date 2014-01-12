package ru.nkdhny.signature.auth

import ru.nkdhny.signature.service.{WithSystemTimeProvider, TimeProvider, WithConfig, WithMongo}
import ru.nkdhny.signature.model.{RegisteredUser, Session, Id}
import scala.util.{Failure, Success, Try}
import com.mongodb.casbah.Imports._
import java.util.UUID
import ru.nkdhny.signature.model
import ru.nkdhny.signature.auth.AuthService.WrongLoginOrPasswordException
import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.commons.conversions.scala._

/**
 * User: alexey
 * Date: 1/11/14
 * Time: 8:06 PM
 */
trait MongoDBAuthService extends AuthService with WithConfig with WithMongo  {

  val credentialsCollection = mongoDb(config.getString("signature.auth.credentials"))
  val sessionsCollection = mongoDb(config.getString("signature.auth.sessions"))
  val sessionTimeout = config.getInt("signature.service.session.timeout").minute

  protected val timeProvider: TimeProvider


  def obtainSession(name: Username, password: Password): Try[Id[Session]] = {
    val s  = for {
      user <- readUserByCredentials(name, password)
    } yield {
      val s = Iterator.continually(guessSession()).find(sessionUnique).map(id => Session(id, user = Option(user)))
      s.foreach(storeSession)
      s
    }

    s.flatten match {
      case Some(s: Session) => Success(s.id)
      case _ => Failure(new WrongLoginOrPasswordException)
    }
  }

  def checkSession(sessionId: Id[Session]): Boolean = {
    import model._
    import Session._

    val updated = for {
      s: Session <- sessionsCollection.one(sessionId) if s.lastAccessed > (timeProvider.now - sessionTimeout)
    } yield {
      val updated = Session(s.id, timeProvider.now, s.user)
      storeSession(updated)
      updated
    }

    updated.isDefined
  }

  def userBySessionId(sessionId: Id[Session]): Option[RegisteredUser] = {
    import model._
    import Session._

    val s: Option[Session] = sessionsCollection.one(sessionId)
    s.flatMap(_.user)
  }


  protected case class Credentials(name: Username, password: Password)
  protected def genPasswordHash(password: Password): String = password

  protected implicit val credentialsDBView: Credentials=>DBObject = c => {
    MongoDBObject(
      "username" -> c.name,
      "passwordHash" -> genPasswordHash(c.password)
    )
  }


  protected def readUserByCredentials(name: Username, password: Password): Option[RegisteredUser] = {
    import model._
    import RegisteredUser._
    for{
      u: RegisteredUser <- credentialsCollection.one(Credentials(name, password))
    } yield {
      u
    }
  }

  protected def guessSession(): Id[Session] = Id(UUID.randomUUID().toString)
  protected def sessionUnique(id: Id[Session]) = {
    import model._
    sessionsCollection.find(id).isEmpty
  }

  protected def storeSession(s: Session) = {
    import model._
    import Session._

    sessionsCollection.remove(s.id, WriteConcern.Safe)
    sessionsCollection.insert(s, WriteConcern.Safe)
  }
}


trait WithMongoDBAuth {
  protected val authService: AuthService = new MongoDBAuthService with WithSystemTimeProvider
}
