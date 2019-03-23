package stateful

import akka.Done
import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble
import BankAccount2._
import stateful.Main.actorSystem

object BankAccount2 {

  sealed trait Action2
  case class Deposit(amount: Int, replyTo: ActorRef[Done])    extends Action2
  case class Withdrawal(amount: Int, replyTo: ActorRef[Done]) extends Action2
  case class GetBalance(replyTo: ActorRef[Int])               extends Action2

  def behaviour(balance: Int): Behavior[Action2] = Behaviors.receiveMessage[Action2] { action =>
    action match {
      case Deposit(amount, replyTo) =>
        println(s"deposting $amount")
        replyTo ! Done
        behaviour(balance + amount)
      case Withdrawal(amount, replyTo) =>
        println(s"withdrawing $amount")
        replyTo ! Done
        behaviour(balance - amount)
      case GetBalance(replyTo) =>
        println(s"current balance is $balance")
        replyTo ! balance
        Behaviors.same
    }
  }
}

class BankAccountProxy(actorRef: ActorRef[Action2])(implicit actorSystem: ActorSystem) {
  implicit val sc: Scheduler    = actorSystem.scheduler
  implicit val timeout: Timeout = Timeout(1.second)

  def deposit(amount: Int): Future[Done] = actorRef ? { ref: ActorRef[Done] =>
    Deposit(amount, ref)
  }
  def withdrawal(amount: Int): Future[Done] = actorRef ? { ref: ActorRef[Done] =>
    Withdrawal(amount, ref)
  }
  def getBalance: Future[Int] = actorRef ? { ref: ActorRef[Int] =>
    GetBalance(ref)
  }
}

object Main extends App {
  val actorSystem                 = ActorSystem("test")
  val actorRef: ActorRef[Action2] = actorSystem.spawnAnonymous(BankAccount2.behaviour(0))
  implicit val sc                 = actorSystem.scheduler
  implicit val timeout            = Timeout(1.second)

  val a: Future[Done] = actorRef ? { ref: ActorRef[Done] =>
    Deposit(100, ref)
  }

  val b: Future[Int] = actorRef ? { ref: ActorRef[Int] =>
    GetBalance(ref)
  }

  actorRef ? { ref: ActorRef[Done] =>
    Withdrawal(100, ref)
  }

  actorRef ? { ref: ActorRef[Int] =>
    GetBalance(ref)
  }
}
