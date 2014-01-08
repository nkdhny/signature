package ru.nkdhny.signature.model

import com.github.nscala_time.time.Imports._
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:20 PM
 */

abstract class UserEvent

case class MousePointerLocationReported(userId: Id[RegisteredUser], time: DateTime, pointer: Location) extends UserEvent

object MongoDbConversions {
  implicit val MousePointerLocationReportedToDbObject: MousePointerLocationReported=>DBObject = l => {
    MongoDBObject(
      "userId"-> l.userId,
      "time" -> l.time.toDate,
      "x" -> l.pointer.x,
      "y" -> l.pointer.y
    )
  }
}