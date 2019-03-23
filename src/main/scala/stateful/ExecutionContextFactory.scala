package stateful
import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps

import scala.concurrent.ExecutionContext

object ExecutionContextFactory {
  def executorServiceBased(): ExecutionContext = {
    val queue: ExecutorService = Executors.newSingleThreadExecutor()
    new ExecutionContext {
      override def execute(runnable: Runnable): Unit     = queue.submit(runnable)
      override def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
    }
  }

  def actorBased(implicit actorSystem: ActorSystem): ExecutionContext = {
    val actorRef = actorSystem.spawnAnonymous(ExecutorActor.behavior)
    fromActor(actorRef)
  }

  def fromActor(actorRef: ActorRef[Runnable]): ExecutionContext = new ExecutionContext {
    override def execute(runnable: Runnable): Unit     = actorRef ! runnable
    override def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
  }
}

object ExecutorActor {
  def behavior: Behavior[Runnable] = Behaviors.receiveMessage { runnable =>
    runnable.run()
    Behaviors.same
  }
}
