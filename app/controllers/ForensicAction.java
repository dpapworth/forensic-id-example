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
    private static final String X_FORENSIC_ID = "X-Forensic-Id";
    private static final String FORENSIC_ID = "forensic-id";

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        String forensicId;
        if (ctx.request().hasHeader(X_FORENSIC_ID)) {
            forensicId = ctx.request().getHeader(X_FORENSIC_ID);
        } else {
            forensicId = generateForensicId();
        }
        MDC.put(FORENSIC_ID, forensicId);

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
