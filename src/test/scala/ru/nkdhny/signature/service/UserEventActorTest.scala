package ru.nkdhny.signature.service

import org.scalatest.{Ignore, FlatSpec}
import org.specs2.matcher.MustMatchers
import akka.actor.{Props, ActorSystem}
import ru.nkdhny.signature.model._
import org.joda.time.DateTime
import com.mongodb.casbah.commons.MongoDBObject

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 7:21 PM
 */

@Ignore
class UserEventActorTest extends FlatSpec with MustMatchers {

  val system = ActorSystem("TestSystem")

  val eventsActor = system.actorOf(Props[UserEventActor])

  "A user events actor" should "process an mouse position message and save it to MDB" in {
    val message = MousePointerLocationReported(Id[RegisteredUser]("qwerty"), Id[Session]("qwerty"), DateTime.now(), Location(1,2))

    eventsActor ! message

    //let async write to complete
    Thread.sleep(100)

    UserEventActor.eventsCollection.find(MongoDBObject("userId"->"qwerty")).size must beEqualTo(1)
  }
}
