package ru.nkdhny.signature.routing

import akka.actor.{ActorRef, Props, Actor}
import spray.routing._
import directives._
import ru.nkdhny.signature.model.{Session, Id, Location, MousePointerLocationReported}
import com.github.nscala_time.time.Imports.{DateTime => Time}
import ru.nkdhny.signature.auth.{WithMongoDBAuth, MongoDBAuthService}
import ru.nkdhny.signature.service.{UserEventActor, WithSystemTimeProvider, WithMongo, WithConfig}
import scala.util.{Failure, Success}
import spray.http.MediaTypes._
import spray.http.HttpHeaders.RawHeader

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class UserServiceActor extends Actor with UserService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  override val eventsActor = context.system.actorOf(Props[UserEventActor])

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}


// this trait defines our service behavior independently from the service actor
trait UserService extends HttpService with WithMongoDBAuth with WithConfig with WithMongo with WithSystemTimeProvider {

  val eventsActor: ActorRef

  val route =
    path("mouse") {
      userFromValidSession {userWithSession => {
          put {
            formFields('x.as[Int], 'y.as[Int]) { (x, y) => {
                complete {
                  val m = MousePointerLocationReported(userWithSession._2.id, userWithSession._1, timeProvider.now, Location(x, y))
                  eventsActor!m
                  "OK"
                }
              }
            }
          }
        }
      }
    }~
    path("session") {
      get {
        parameters('username, 'password) { (name, pwd) => {

            authService.obtainSession(name, pwd) match {
              case Success(sessionId: Id[Session]) => {
                respondWithMediaType(`application/json`) {
                  respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
                    complete {
                      s"""{"sessionId": "$sessionId"}"""
                    }
                  }
                }
              }
              case Failure(t: Throwable) => reject
            }
          }
        }
      }
    }
}