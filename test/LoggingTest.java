import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class LoggingTest {

    private Application app;

    @Before
    public void setup() {
        app = fakeApplication(inMemoryDatabase());
    }

    @Test
    public void addForensicIdToRequest() {
        running(app, () -> {
            Result result = route(app, fakeRequest(GET, "/"));

            assertEquals(Http.Status.OK, result.status());
        });
    }

    @Test
    public void useForensicIdInRequest() {
        running(app, () -> {
            Result result = route(app, fakeRequest(GET, "/").header("X-Forensic-Id", "1234"));

            assertEquals(Http.Status.OK, result.status());
        });
    }
}
