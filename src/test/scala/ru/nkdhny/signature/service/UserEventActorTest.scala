package ru.nkdhny.signature.service

import org.scalatest.{Ignore, FlatSpec}
import akka.actor.{Props, ActorSystem}
import ru.nkdhny.signature.model._
import org.joda.time.DateTime
import com.mongodb.casbah.commons.MongoDBObject
import org.scalatest.Matchers._

/**
 * User: alexey
 * Date: 1/8/14
 * Time: 7:21 PM
 */

//@Ignore
class UserEventActorTest extends FlatSpec {

  val system = ActorSystem("TestSystem")

  val eventsActor = system.actorOf(Props[UserEventActor])

  "A user events actor" should "process an mouse position message and save it to MDB" in {
    UserEventActor.eventsCollection.remove(MongoDBObject())
    val message = MousePointerLocationReported(Id[RegisteredUser]("qwerty"), Id[Session]("qwerty"), DateTime.now(), Location(1,2))

    eventsActor ! message

    //let async write to complete
    Thread.sleep(100)

    UserEventActor.eventsCollection.find(MongoDBObject("userId"->"qwerty")).size should be(1)
  }

  it should "produce a test data" in {
    UserEventActor.eventsCollection.remove(MongoDBObject())
    for {i <- 1 to 100} {
      val message = MousePointerLocationReported(Id[RegisteredUser]("qwerty"), Id[Session]("qwerty"), new DateTime(i*1000), Location(i, 2*i))
      eventsActor ! message
    }
    Thread.sleep(100)
    UserEventActor.eventsCollection.size should be(100)
  }
}
