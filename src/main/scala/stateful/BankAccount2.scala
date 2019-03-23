package stateful

import akka.actor.ActorSystem
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps

object BankAccount2 {

  def behaviour(balance: Int): Behavior[Action] = Behaviors.receiveMessage[Action] { action =>
    action match {
      case Deposit(amount) =>
        println(s"deposting $amount")
        behaviour(balance + amount)
      case Withdrawal(amount) =>
        println(s"withdrawing $amount")
        behaviour(balance - amount)
      case GetBalance() =>
        println(s"current balance is $balance")
        Behaviors.same
    }
  }
}

object Main extends App {
  val actorSystem                = ActorSystem("test")
  val actorRef: ActorRef[Action] = actorSystem.spawnAnonymous(BankAccount2.behaviour(0))

  actorRef ! Deposit(100)
  actorRef ! GetBalance()
  actorRef ! Withdrawal(100)
  actorRef ! GetBalance()

}
