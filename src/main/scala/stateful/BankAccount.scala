package stateful

import java.util.concurrent.atomic.AtomicReference

case class AccountData(balance: Int, actions: List[Action])

class BankAccount(externalService: ExternalService) {

  private val ref: AtomicReference[AccountData] = {
    new AtomicReference(AccountData(0, Nil))
  }

  def deposit(amount: Int): Unit = {
    externalService.record2 { () =>
      ref.updateAndGet { x =>
        AccountData(x.balance + amount, Deposit(amount) :: x.actions)
      }
    }

  }

  def withdraw(amount: Int): Unit = {
    externalService.record2 { () =>
      ref.updateAndGet { x =>
        AccountData(x.balance - amount, Withdrawal(amount) :: x.actions)
      }
    }
  }

  def balance: Int = {
    ref.get().balance
  }
}
