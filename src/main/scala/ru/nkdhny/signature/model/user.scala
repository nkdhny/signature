package ru.nkdhny.signature.model

import com.mongodb.casbah.Imports._
import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.commons.conversions.scala._

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:30 PM
 */
abstract class User

case class RegisteredUser(id: Id[RegisteredUser]) extends User

object RegisteredUser {
  implicit val userObjView: DBObject=>Option[RegisteredUser] = d => {
    idObjView[RegisteredUser](d).map(RegisteredUser(_))
  }

  implicit val userDBView: RegisteredUser => DBObject = u => {
    idDbView(u.id)
  }
}

case class Session(id: Id[Session], lastAccessed: DateTime = DateTime.now, user: Option[RegisteredUser])

object Session {

  implicit val sessionObjView: DBObject=>Option[Session] = d => {
    for{
      id <- idObjView[Session](d)
      accessed <- d.getAs[DateTime]("accessed")
    } yield {
      Session(id, accessed, d.getAs[DBObject]("user").flatMap(RegisteredUser.userObjView))
    }
  }
  implicit val sessionDbView: Session => DBObject = s => {
    val m = idDbView(s.id)
    m += "accessed"->s.lastAccessed
    s.user.map(u => RegisteredUser.userDBView(u)).foreach(m+= "user" -> _)

    m
  }

}
