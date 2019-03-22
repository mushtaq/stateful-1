package stateful
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.Success

class ExternalService(blockingEc: ExecutionContext) {

  private val queue: ScheduledExecutorService = {
    Executors.newScheduledThreadPool(10)
  }

  def record(action: Action): Unit =
    Future.unit.map { _ =>
      Thread.sleep(1000)
    }(blockingEc)

  def asyncNonBlockingCall(callback: Runnable): Unit = {
    queue.schedule(callback, 1, TimeUnit.SECONDS)
  }

  def asyncNonBlockingCall2(): Future[Unit] = tick(1.second)

  def tick(duration: FiniteDuration): Future[Unit] = {
    val p: Promise[Unit] = Promise()
    val runnable: Runnable = { () =>
      p.complete(Success(()))
    }
    queue.schedule(runnable, duration.length, duration.unit)
    p.future
  }

  def asyncBlockingCall(callback: Runnable): Unit = {
    val runnable: Runnable = () => { Thread.sleep(1000); callback.run() }
    queue.submit(runnable)
  }

}
