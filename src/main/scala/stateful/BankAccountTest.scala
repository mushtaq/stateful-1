package stateful
import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

object BankAccountTest extends App {

  private val blockingEC: ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(300))
  }

  private val externalService = new ExternalService(blockingEC)
  private val bankAccount     = new BankAccount(externalService)

  implicit val ec: ExecutionContext = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

  val finalFuture = Future
    .traverse((1 to 10000).toList) { x =>
      val f1 = Future.unit.flatMap { _ =>
        bankAccount.deposit(10)
      }
      val f2 = Future.unit.flatMap { _ =>
        bankAccount.withdraw(10)
      }
      f1.flatMap(_ => f2)
    }
    .flatMap(_ => bankAccount.balance)

  finalFuture.onComplete(println)
}
