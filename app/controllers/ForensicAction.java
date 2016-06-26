package controllers;

import org.slf4j.MDC;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Action to add a forensic-id to MDC.
 */
public class ForensicAction extends Action.Simple {
    private static final String FORENSIC_ID = "forensic-id";

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        MDC.put(FORENSIC_ID, generateForensicId());

        try {
            return delegate.call(ctx);
        } finally {
            MDC.remove(FORENSIC_ID);
        }
    }

    protected String generateForensicId() {
        return UUID.randomUUID().toString();
    }
}
