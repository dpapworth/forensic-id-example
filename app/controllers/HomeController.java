package controllers;

import monitoring.MDCHttpExecutionContext;
import org.slf4j.MDC;
import play.Logger;
import play.mvc.*;

import views.html.*;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    private MDCHttpExecutionContext ec;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index() {
        MDC.put("forensic-id", UUID.randomUUID().toString());

        try {
            Logger.info("Serving index page");

            return supplyAsync(() -> {
                Logger.info("Generating response");
                return "Your new application is ready.";
            }, ec.current()).thenApply(message -> ok(index.render(message)));
        } finally {
            MDC.remove("forensic-id");
        }
    }

}
