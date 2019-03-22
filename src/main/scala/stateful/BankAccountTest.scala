package stateful
import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

object BankAccountTest extends App {

  private val externalService = new ExternalService
  private val bankAccount     = new BankAccount(externalService)

  implicit val ec: ExecutionContext = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

  val finalFuture = Future.traverse((1 to 10000).toList) { x =>
    val f1 = Future.unit.map { _ =>
      bankAccount.deposit(10)
    }
    val f2 = Future.unit.map { _ =>
      bankAccount.withdraw(10)
    }
    f1.flatMap(_ => f2)
  }

  Thread.sleep(2000)

  finalFuture.onComplete { _ =>
    bankAccount.onBalance { balance =>
      println(balance)
    }
  }
}
