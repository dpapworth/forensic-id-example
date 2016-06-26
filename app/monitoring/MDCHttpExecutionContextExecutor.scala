package monitoring

import org.slf4j.MDC
import scala.concurrent.{ExecutionContextExecutor, ExecutionContext}

/**
  * Adapted from http://yanns.github.io/blog/2014/05/04/slf4j-mapped-diagnostic-context-mdc-with-play-framework/
  *
  * slf4j provides a MDC [[http://logback.qos.ch/manual/mdc.html Mapped Diagnostic Context]]
  * based on a [[ThreadLocal]]. In an asynchronous environment, the callbacks can be called
  * in another thread, where the local thread variable does not exist anymore.
  *
  * This execution context fixes this problem:
  * it propagates the MDC from the caller's thread to the callee's one.
  */
object MDCHttpExecutionContextExecutor {

  /**
    * Create an MDCHttpExecutionContext with values from the current thread.
    */
  def fromThread(delegate: ExecutionContext): ExecutionContextExecutor =
    new MDCHttpExecutionContextExecutor(MDC.getCopyOfContextMap, delegate)

}

/**
  * Manages execution to ensure that the given MDC context are set correctly
  * in the current thread. Actual execution is performed by a delegate ExecutionContext.
  */
class MDCHttpExecutionContextExecutor(mdcContext: java.util.Map[String, String], delegate: ExecutionContext) extends ExecutionContextExecutor {
  def execute(runnable: Runnable) = delegate.execute(new Runnable {
    def run() {
      val oldMDCContext = MDC.getCopyOfContextMap
      setContextMap(mdcContext)
      try {
        runnable.run()
      } finally {
        setContextMap(oldMDCContext)
      }
    }
  })

  private[this] def setContextMap(context: java.util.Map[String, String]) {
    if (context == null) {
      MDC.clear()
    } else {
      MDC.setContextMap(context)
    }
  }

  def reportFailure(t: Throwable) = delegate.reportFailure(t)
}
