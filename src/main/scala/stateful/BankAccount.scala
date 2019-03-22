package stateful

class BankAccount {
  private var _balance              = 0
  private var actions: List[Action] = Nil

  def deposit(amount: Int): Unit = {
    _balance += amount
    actions ::= Deposit(amount)
  }

  def withdraw(amount: Int): Unit = {
    _balance -= amount
    actions = new Withdrawal(amount) :: actions
  }

  def balance: Int = synchronized {
    _balance
  }

  def get: (Int, List[Action]) = {
    (_balance, actions)
  }
}
