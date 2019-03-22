package stateful
import java.util.concurrent.{ExecutorService, Executors}

class CompositeQueryFail(pricingService: PricingService) {
  private val queue: ExecutorService = Executors.newSingleThreadExecutor()

  def onTotalPrice(f: Int => Unit): Unit = {
    var prices = List.empty[Int]

    def dd(): Unit = if (prices.length == 2) {
      f(prices.sum)
    }

    pricingService.onProductPrice(1, { p1 =>
      val runnable: Runnable = { () =>
        prices ::= p1
        dd()
      }
      queue.submit(runnable)
    })

    pricingService.onProductPrice(2, { p2 =>
      val runnable: Runnable = { () =>
        prices ::= p2
        dd()
      }
      queue.submit(runnable)
    })
  }
}
