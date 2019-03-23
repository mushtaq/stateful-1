package stateful
import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.ExecutionContext

object ExecutionContextFactory {
  def executorServiceBased(): ExecutionContext = {
    val queue: ExecutorService = Executors.newSingleThreadExecutor()
    new ExecutionContext {
      override def execute(runnable: Runnable): Unit     = queue.submit(runnable)
      override def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
    }
  }

  def actorBased(): ExecutionContext = {
    ???
  }
}
