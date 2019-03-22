package stateful

import java.util.concurrent.{ExecutorService, Executors}

class BankAccount(externalService: ExternalService) {

  private var _balance = 0
  private var _actions = List.empty[Action]

  private val queue: ExecutorService = Executors.newSingleThreadExecutor()

  def deposit(amount: Int): Unit = {
    externalService.record2 { () =>
      val callback: Runnable = { () =>
        _balance += amount
        _actions ::= Deposit(amount)
      }
      queue.submit(callback)
    }
  }

  def withdraw(amount: Int): Unit = {
    externalService.record2 { () =>
      val callback: Runnable = { () =>
        _balance -= amount
        _actions ::= Withdrawal(amount)
      }
      queue.submit(callback)
    }
  }

  def onBalance(callback: Int => Unit): Unit = {
    val runnable: Runnable = () => callback(_balance)
    queue.submit(runnable)
  }
}
