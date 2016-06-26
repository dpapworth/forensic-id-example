import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class LoggingTest {

    private Application app;

    @Before
    public void setup() {
        app = fakeApplication(inMemoryDatabase());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addForensicIdToRequest() {
        running(app, () -> {
            ListAppender<ILoggingEvent> appender = attachAppender();

            Result result = route(app, fakeRequest(GET, "/"));

            assertEquals(Http.Status.OK, result.status());

            assertThat(appender.list, containsInAnyOrder(
                    logLine(equalTo("INFO"), equalTo("Serving index page"), hasMDC("forensic-id")),
                    logLine(equalTo("INFO"), equalTo("Generating response"), hasMDC("forensic-id"))
            ));

            detachAppender(appender);
        });

    }

    @Test
    @SuppressWarnings("unchecked")
    public void useForensicIdInRequest() {
        running(app, () -> {
            ListAppender<ILoggingEvent> appender = attachAppender();

            Result result = route(app, fakeRequest(GET, "/").header("X-Forensic-Id", "1234"));

            assertEquals(Http.Status.OK, result.status());
            System.out.println("Found: " + appender.list.size());
            appender.list.forEach(System.out::println);

            assertThat(appender.list, containsInAnyOrder(
                    logLine(equalTo("INFO"), equalTo("Serving index page"), hasMDC("forensic-id", "1234")),
                    logLine(equalTo("INFO"), equalTo("Generating response"), hasMDC("forensic-id", "1234"))
            ));

            detachAppender(appender);
        });
    }

    private ListAppender<ILoggingEvent> attachAppender() {


        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setName("test");
        appender.start();
        getRootLogger().addAppender(appender);
        return appender;
    }

    private void detachAppender(ListAppender<ILoggingEvent> appender) {
        getRootLogger().detachAppender(appender);
        appender.stop();
    }

    private Logger getRootLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    private Matcher<ILoggingEvent> logLine(Matcher<String> levelMatcher, Matcher<String> messageMatcher, Matcher<Map<? extends String, ? extends String>> mdcMatcher) {
        return allOf(
                new FeatureMatcher<ILoggingEvent, String>(levelMatcher, "level", "level") {
                    @Override
                    protected String featureValueOf(ILoggingEvent actual) {
                        return actual.getLevel().toString();
                    }
                },
                new FeatureMatcher<ILoggingEvent, String>(messageMatcher, "message", "message") {
                    @Override
                    protected String featureValueOf(ILoggingEvent actual) {
                        return actual.getMessage();
                    }
                },
                new FeatureMatcher<ILoggingEvent, Map<String, String>>(mdcMatcher, "mdc", "mdc") {
                    @Override
                    protected Map<String, String> featureValueOf(ILoggingEvent actual) {
                        return actual.getMDCPropertyMap();
                    }
                }
        );
    }

    private Matcher<Map<? extends String, ? extends String>> hasMDC(String key) {
        return hasEntry(equalTo(key), notNullValue(String.class));
    }

    private Matcher<Map<? extends String, ? extends String>> hasMDC(String key, String value) {
        return hasEntry(equalTo(key), equalTo(value));
    }
}
