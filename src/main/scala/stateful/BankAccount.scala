package stateful

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}

import scala.async.Async._
import scala.concurrent.{ExecutionContext, Future}

class BankAccount(externalService: ExternalService)(implicit actorSystem: ActorSystem) {

  private var _balance = 0
  private var _actions = List.empty[Action]

  implicit val mat: Materializer    = ActorMaterializer()
  implicit val ec: ExecutionContext = ExecutionContextFactory.streamBased
//  implicit val ec: ExecutionContext = ExecutionContextFactory.actorBased

  private val (queue, _stream) = {
    Source.queue[Action](1024000, OverflowStrategy.dropTail).preMaterialize()
  }

  def deposit(amount: Int): Future[Unit] = async {
    val _ = await(externalService.asyncNonBlockingCall2())
    _balance += amount
    _actions ::= Deposit(amount)
    queue.offer(Deposit(amount))
  }

  def withdraw(amount: Int): Future[Unit] = Future.unit.flatMap { _ =>
    externalService.asyncNonBlockingCall2().map { _ =>
      _balance -= amount
      _actions ::= Withdrawal(amount)
      queue.offer(Withdrawal(amount))
    }
  }

  def balance: Future[Int] = async {
    _balance
  }

  def stream: Source[Action, NotUsed] = _stream
}
