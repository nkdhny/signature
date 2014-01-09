package ru.nkdhny.signature.routing

import akka.actor.Actor
import spray.routing._
import spray.http._
import directives._
import ru.nkdhny.signature.model.{Location, MousePointerLocationReported}
import com.github.nscala_time.time.Imports.{DateTime => Time}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class UserServiceActor extends Actor with UserService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}


// this trait defines our service behavior independently from the service actor
trait UserService extends HttpService {

  val eventsActor = ???

  val route =
    path("event/pointer") {
      userFromValidSession {userWithSession => {
          put {
            formFields('time.as[Long], 'x.as[Int], 'y.as[Int]) { (time, x, y) => {
                complete {
                  val m = MousePointerLocationReported(userWithSession._2.id, userWithSession._1, new Time(time), Location(x, y))
                  //eventsActor!m
                  "Event received"
                }
              }
            }
          }
        }
      }
    }
}