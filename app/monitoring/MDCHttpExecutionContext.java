package monitoring;

import scala.concurrent.ExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Singleton
public class MDCHttpExecutionContext {

    private final Executor delegate;

    @Inject
    public MDCHttpExecutionContext(Executor delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the current executor associated with the current HTTP context.
     *
     * Note that the returned executor is only valid for the current context.  It should be used in a transient
     * fashion, long lived references to it should not be kept.
     *
     * @return An executor that will execute its tasks in the current HTTP context.
     */
    public Executor current() {
        return MDCHttpExecutionContextExecutor.fromThread((ExecutionContext) delegate);
    }
}