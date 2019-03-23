package stateful

import java.util.concurrent.Executors

import akka.actor.ActorSystem

import scala.async.Async._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.control.NonFatal

object BankAccountTest extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("test")

  private val blockingEC: ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(300))
  }

  private val externalService = new ExternalService(blockingEC)
  private val bankAccount     = new BankAccount(externalService)

  implicit val ec: ExecutionContext = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

  val finalFuture = Future.traverse((1 to 10000).toList) { x =>
    val f1 = async {
      await(bankAccount.deposit(10))
    }
    val f2 = async {
      await(bankAccount.withdraw(10))
    }
    async {
      await(f1)
      await(f2)
    }
  }

  async {
    await(finalFuture)
    val balance = await(bankAccount.balance)
    println(balance)
  }.recover {
    case NonFatal(ex) => ex.printStackTrace()
  }
}
