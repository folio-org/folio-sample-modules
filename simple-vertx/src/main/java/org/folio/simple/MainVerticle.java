package org.folio.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
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
public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    /**
     * All services of one Verticle instance should share a single HttpClient (or WebClient)
     * to allow for HTTP pooling and HTTP pipe-lining and to avoid HttpClient socket leaks.
     */
    final HttpClient httpClient = vertx.createHttpClient();
    final SimpleWebService simple = new SimpleWebService(httpClient);

    final int port = Integer.parseInt(System.getProperty("port", "8080"));
    logger.info("Starting simple "
            + ManagementFactory.getRuntimeMXBean().getName()
            + " on port " + port);

    // Define the routes for HTTP requests. Both GET and POST go to the same
    // one here...
    Router router = Router.router(vertx);
    router.get("/simple").handler(simple::get_handle);
    router.post("/*").handler(BodyHandler.create()); // Tell vertx we want to the whole POST body in the handler
    router.post("/simple").handler(simple::post_handle);

    // And start listening
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port)
      .onSuccess(x -> logger.debug("Succeeded in starting the listener for simple"))
      .onFailure(e -> logger.error("simple failed: " + e.getMessage(), e))
      .<Void>mapEmpty()
      .onComplete(startPromise);
  }
}
