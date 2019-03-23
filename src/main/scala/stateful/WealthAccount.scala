package stateful
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}

class WealthAccount(accounts: List[BankAccount])(implicit actorSystem: ActorSystem) {

  implicit val mat: Materializer    = ActorMaterializer()
  implicit val ec: ExecutionContext = ExecutionContextFactory.streamBased

  private var _balance = 0

  val stream: Source[Action, NotUsed] = accounts
    .map(account => account.stream)
    .foldLeft(Source.empty[Action]) { (acc, elm) =>
      acc.merge(elm)
    }

  val balanceStream: Source[Int, NotUsed] = stream.scan(0) {
    case (acc, Deposit(amount))    => acc + amount
    case (acc, Withdrawal(amount)) => acc - amount
  }

  private def update(b: Int): Future[Unit] = Future {
    _balance = b
  }

  balanceStream.mapAsync(1)(update).runForeach(_ => ())

//  balanceStream.runForeach(update)

  def balance: Future[Int] = Future(_balance)
}
