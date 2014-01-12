package ru.nkdhny.signature.service

import akka.actor.Actor

import ru.nkdhny.signature.model.MousePointerLocationReported



/**
 * User: alexey
 * Date: 1/8/14
 * Time: 6:42 PM
 */
object UserEventActor extends WithConfig with WithMongo {
  private[service] lazy val eventsCollection = mongoDb(config.getString("signature.mongodb.eventsCollection"))
}

class UserEventActor extends Actor {

  import UserEventActor._

  def receive = {

    case position: MousePointerLocationReported => eventsCollection.insert(position)
  }

}
