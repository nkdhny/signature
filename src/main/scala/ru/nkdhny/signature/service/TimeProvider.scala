package ru.nkdhny.signature.service
import com.github.nscala_time.time.Imports._



/**
 * User: alexey
 * Date: 1/12/14
 * Time: 12:54 PM
 */
trait TimeProvider {

  def now: DateTime
}

class TestTimeProvider(var time: DateTime) extends TimeProvider {
  def now: DateTime = time
}

trait WithSystemTimeProvider {
  protected val timeProvider: TimeProvider = new TimeProvider {
    def now: DateTime = DateTime.now
  }
}
