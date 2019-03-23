package stateful

sealed trait Action
case class Deposit(amount: Int)    extends Action
case class Withdrawal(amount: Int) extends Action
case class GetBalance()            extends Action
