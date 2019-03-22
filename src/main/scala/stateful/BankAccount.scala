package stateful

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

class BankAccount(externalService: ExternalService) {

  private var _balance = 0
  private var _actions = List.empty[Action]

  implicit val ec: ExecutionContext = {
    val queue: ExecutorService = Executors.newSingleThreadExecutor()
    ExecutionContext.fromExecutorService(queue)
  }

  def deposit(amount: Int): Future[Unit] = Future.unit.flatMap { _ =>
    externalService.asyncNonBlockingCall2().map { _ =>
      _balance += amount
      _actions ::= Deposit(amount)
    }
  }

  def withdraw(amount: Int): Future[Unit] = Future.unit.flatMap { _ =>
    externalService.asyncNonBlockingCall2().map { _ =>
      _balance -= amount
      _actions ::= Withdrawal(amount)
    }
  }

  def balance: Future[Int] = Future.unit.map { _ =>
    _balance
  }
}
