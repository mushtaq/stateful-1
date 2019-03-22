package stateful

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

class ExploreApi(pricingService: PricingService)(implicit ec: ExecutionContext) {

  val futures: List[Future[Int]] = (1 to 10).toList.map(x => pricingService.getProductPrice(x))
  val future: Future[List[Int]]  = Future.sequence(futures)

  val rr: Future[List[Int]] = Future.traverse((1 to 10).toList) { x =>
    pricingService.getProductPrice(x)
  }

  val failedFutures: List[Future[Throwable]] = futures.map(_.failed)
  val future2: Future[List[Throwable]]       = Future.sequence(failedFutures)

  val dd: List[Future[Try[Int]]] = futures.map { f =>
    f.map(x => Success(x)).recover {
      case NonFatal(ex) => Failure(ex)
    }
  }

  private val ee: Future[List[Try[Int]]] = Future.sequence(dd)

  private val aa: Future[List[Try[Int]]] = ee.map(_.filter(_.isFailure))
  private val bb: Future[List[Try[Int]]] = ee.map(_.filter(_.isSuccess))

  /////////////
  Future {
    1 + 10
  }

  Future {
    1 / 0
  }

  Future.unit.map(_ => 1 + 10)
  Future.unit.map(_ => 1 / 0)
  /////////////////

  Future.successful(1 + 10)
  Future.successful(1 / 0)
  Future.failed[Int](new RuntimeException)

  (): Unit

  Future.successful(())

  val f: Future[Int]                = Future.unit.map(_ => 1 + 10)
  private val f2: Future[Throwable] = f.failed

  val f3: Future[Int]               = Future.unit.map(_ => 1 / 0)
  private val f4: Future[Throwable] = f.failed

}
