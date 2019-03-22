package stateful

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CompositeQuery(pricingService: PricingService) {

  def getTotalPrice: Future[Int] = {
    pricingService.getProductPrice(1).flatMap { p1 =>
      pricingService.getProductPrice(2).map { p2 =>
        p1 + p2
      }
    }
  }

  def getTotalPrice2: Future[Int] = {
    val f1: Future[Int] = pricingService.getProductPrice(1)
    val f2: Future[Int] = pricingService.getProductPrice(2)
    f1.flatMap { p1: Int =>
      f2.map { p2: Int =>
        p1 + p2
      }
    }
  }
}
