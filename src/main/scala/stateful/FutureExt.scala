package stateful
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object FutureExt {
  implicit class RichFuture[T](val f: Future[T]) extends AnyVal {
    def get: T = Await.result(f, 5.seconds)
  }
}
