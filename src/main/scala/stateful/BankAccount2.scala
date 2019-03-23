package stateful

import java.util.concurrent.Executors

import akka.Done
import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.concurrent.duration.DurationDouble
import BankAccount2._
import stateful.FutureExt.RichFuture

import scala.util.{Failure, Success, Try}

object BankAccount2 {

  sealed trait Action2
  case class Deposit2(amount: Int, replyTo: ActorRef[Try[Done]])           extends Action2
  case class Withdrawal2(amount: Int, replyTo: ActorRef[Done])             extends Action2
  private case class SelfWithdrawal2(amount: Int, replyTo: ActorRef[Done]) extends Action2
  case class GetBalance2(replyTo: ActorRef[Int])                           extends Action2

  def dd(externalService: ExternalService): Behavior[Action2] = Behaviors.setup[Action2] { ctx =>
    import ctx.executionContext

    def behaviour(balance: Int): Behavior[Action2] = Behaviors.receiveMessage[Action2] {
      case Deposit2(amount, replyTo) =>
        val total = try {
          val dd = balance + amount
          throw new NullPointerException("asdasd")
          replyTo ! Success(Done)
          dd
        } catch {
          case ex: NullPointerException =>
            replyTo ! Failure(ex)
            balance
        }
        behaviour(total)
      case Withdrawal2(amount, replyTo) =>
        externalService.asyncNonBlockingCall2().foreach { _ =>
          ctx.self ! SelfWithdrawal2(amount, replyTo)
        }
        Behaviors.same
      case SelfWithdrawal2(amount, replyTo) =>
        replyTo ! Done
        behaviour(balance - amount)
      case GetBalance2(replyTo) =>
        replyTo ! balance
        Behaviors.same
    }

    behaviour(0)
  }
}

class BankAccountProxy(actorRef: ActorRef[Action2])(implicit actorSystem: ActorSystem) {
  implicit val sc: Scheduler    = actorSystem.scheduler
  implicit val timeout: Timeout = Timeout(1.second)

  def deposit(amount: Int): Future[Try[Done]] = actorRef ? { ref: ActorRef[Try[Done]] =>
    Deposit2(amount, ref)
  }
  def withdrawal(amount: Int): Future[Done] = actorRef ? { ref: ActorRef[Done] =>
    Withdrawal2(amount, ref)
  }
  def getBalance: Future[Int] = actorRef ? { ref: ActorRef[Int] =>
    GetBalance2(ref)
  }
}

object Main extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("test")
//  Behaviors
//    .supervise(
//      Behaviors
//        .supervise(BankAccount2.behaviour(0))
//        .onFailure[NullPointerException](SupervisorStrategy.restart)
//    )
//    .onFailure[RuntimeException](SupervisorStrategy.stop)

  private val blockingEC: ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(300))
  }

  private val externalService     = new ExternalService(blockingEC)
  val actorRef: ActorRef[Action2] = actorSystem.spawnAnonymous(BankAccount2.dd(externalService))

  val bankAccountProxy = new BankAccountProxy(actorRef)
  println(bankAccountProxy.deposit(100).get)
  println(bankAccountProxy.getBalance.get)
  println(bankAccountProxy.withdrawal(100).get)
  println(bankAccountProxy.getBalance.get)
}
