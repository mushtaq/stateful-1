package stateful
import java.util.concurrent.{ExecutorService, Executors}

class CompositeQuery(externalService: ExternalService) {
  private val queue: ExecutorService = Executors.newSingleThreadExecutor()

  def totalPrice(f: Int => Unit): Unit = {
    var prices = List.empty[Int]

    def dd(): Unit = if (prices.length == 2) {
      f(prices.sum)
    }

    externalService.onProductPrice(1, { p1 =>
      val runnable: Runnable = { () =>
        prices ::= p1
        dd()
      }
      queue.submit(runnable)
    })

    externalService.onProductPrice(2, { p2 =>
      val runnable: Runnable = { () =>
        prices ::= p2
        dd()
      }
      queue.submit(runnable)
    })
  }
}
