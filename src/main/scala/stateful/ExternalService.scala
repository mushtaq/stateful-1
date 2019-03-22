package stateful
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

class ExternalService {

  private val queue: ScheduledExecutorService = {
    Executors.newScheduledThreadPool(10)
  }

//  def record(action: Action): Unit = {
//    Thread.sleep(1000)
//  }

  def record2(callback: Runnable): Unit = {
    queue.schedule(callback, 1, TimeUnit.SECONDS)
  }

  def record3(callback: Runnable): Unit = {
    val runnable: Runnable = () => { Thread.sleep(1000); callback.run() }
    queue.submit(runnable)
  }

  val m = Map(1 -> 100, 2 -> 200)

  def onProductPrice(productId: Int, f: Int => Unit): Unit = {
    val runnable: Runnable = () => f(m(productId))
    queue.schedule(runnable, 1, TimeUnit.SECONDS)
  }
}
