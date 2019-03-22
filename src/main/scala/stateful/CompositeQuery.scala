package stateful

import scala.concurrent.{ExecutionContext, Future}
import async.Async._

class CompositeQuery(pricingService: PricingService)(implicit ec: ExecutionContext) {

  def getTotalPrice: Future[Int] = async {
    val p1 = await(pricingService.getProductPrice(1))
    val p2 = await(pricingService.getProductPrice(2))
    p1 + p2
  }

  def getTotalPrice2: Future[Int] = async {
    val f1: Future[Int] = pricingService.getProductPrice(1)
    val f2: Future[Int] = pricingService.getProductPrice(2)
    await(f1) + await(f2)
  }
}
