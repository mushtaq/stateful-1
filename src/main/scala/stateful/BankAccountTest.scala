package stateful
import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

object BankAccountTest extends App {

  private val bankAccount = new BankAccount

  implicit val ec: ExecutionContext = {
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(100))
  }

  (1 to 1000).foreach { x =>
    Future.unit.foreach { _ =>
      bankAccount.deposit(10)
    }
    Future.unit.foreach { _ =>
      bankAccount.withdraw(10)
    }
  }

  Thread.sleep(2000)

  println(bankAccount.balance)
}
