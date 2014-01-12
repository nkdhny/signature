package ru.nkdhny.signature.auth

import org.scalatest.FlatSpec
import ru.nkdhny.signature.service.{TestTimeProvider, TimeProvider}
import com.github.nscala_time.time.Imports._
import ru.nkdhny.signature.model.{Session, RegisteredUser, Id}
import org.scalatest.Matchers._
import scala.util.Success

/**
 * User: alexey
 * Date: 1/12/14
 * Time: 12:59 PM
 */
class MongoDBAuthServiceTest extends FlatSpec {

  val provider = new TestTimeProvider(new DateTime(0L))

  val service = new MongoDBAuthService {
    protected val timeProvider: TimeProvider = provider
    override protected def readUserByCredentials(name: Username, password: Password): Option[RegisteredUser] = {
      Option(RegisteredUser(Id("a.golomedov")))
    }
  }


  "MongoDB auth service" should "generate valid session" in {
    val s  = service.obtainSession("a.golomedov", "qwerty")

    s shouldBe a [Success[Id[Session]]]
    val id = s.get
    id.size should be >0

    service.checkSession(id) should be (true)

    val user = service.userBySessionId(id)
    user.isDefined should be (true)
    user.get.id.toString should be ("a.golomedov")
  }
  it should "make session not valid after a while" in {

    val s  =  service.obtainSession("a.golomedov", "qwerty")
    s shouldBe a [Success[Id[Session]]]
    val id = s.get
    service.checkSession(id) should be (true)

    provider.time+= 100500.hours

    service.checkSession(id) should be (false)

  }




}
