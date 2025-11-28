package org.folio.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main verticle. This is the HTTP server that accepts incoming requests and
 * routes them to the relevant handlers.
 */
public class MainVerticle extends VerticleBase {

  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  @Override
  public Future<?> start() {
    /**
     * All services of one Verticle instance should share a single HttpClient (or WebClient)
     * to allow for HTTP pooling and HTTP pipe-lining and to avoid HttpClient socket leaks.
     */
    final HttpClient httpClient = vertx.createHttpClient();
    final SimpleWebService simple = new SimpleWebService(httpClient);

    var port = Integer.parseInt(System.getProperty("port", "8080"));
    LOGGER.info("Starting simple {} on port {}",
        ManagementFactory.getRuntimeMXBean().getName(),
        port);

    // Define the routes for HTTP requests. Both GET and POST go to the same
    // one here...
    var router = Router.router(vertx);
    router.get("/simple").handler(simple::getHandle);
    router.post("/*").handler(BodyHandler.create()); // Tell vertx we want the whole POST body in the handler
    router.post("/simple").handler(simple::postHandle);

    // And start listening
    return vertx.createHttpServer()
        .requestHandler(router)
        .listen(port)
        .onSuccess(x -> LOGGER.debug("Succeeded in starting the listener for simple"))
        .onFailure(e -> LOGGER.error("simple failed: {}", e.getMessage(), e));
  }
}
