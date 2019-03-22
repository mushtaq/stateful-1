package stateful
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class PricingService(externalService: ExternalService)(implicit ec: ExecutionContext) {
  private val m = Map(1 -> 100, 2 -> 200)

  def onProductPrice(productId: Int, f: Int => Unit): Unit = {
    val runnable: Runnable = () => f(m(productId))
    externalService.asyncNonBlockingCall(runnable)
  }

  def getProductPrice(productId: Int): Future[Int] = {
    externalService
      .asyncNonBlockingCall2()
      .map(_ => m(productId))
      .recover {
        case NonFatal(ex) =>
          ex.printStackTrace()
          throw ex
      }
  }
}
