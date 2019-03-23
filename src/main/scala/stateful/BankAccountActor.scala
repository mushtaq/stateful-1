package stateful
import akka.Done
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import stateful.BankAccount2.{Action2, Withdrawal2}

import scala.concurrent.ExecutionContext

object BankAccountActor {

  def behavior(externalService: ExternalService): Behavior[Action2] = Behaviors.setup[Action2] { ctx =>
    var balance = 0
//    import ctx.executionContext
    implicit val ec: ExecutionContext = ExecutionContextFactory.executorServiceBased()

    Behaviors.receiveMessage {
      case Withdrawal2(amount, replyTo) =>
        externalService.asyncNonBlockingCall2().foreach { _ =>
          balance -= amount
          replyTo ! Done
        }
        Behaviors.same
    }

  }
}
