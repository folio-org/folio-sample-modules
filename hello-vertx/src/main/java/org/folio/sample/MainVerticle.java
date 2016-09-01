package org.folio.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import static org.folio.okapi.common.HttpResponse.*;

/**
 * The main verticle. This is the HTTP server that accepts incoming requests and
 * routes them to the relevant handlers.
 */
public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger("folio-sample");

  @Override
  public void start(Future<Void> fut) throws IOException {

    final int port = Integer.parseInt(System.getProperty("port", "8080"));
    logger.info("Starting hello "
            + ManagementFactory.getRuntimeMXBean().getName()
            + " on port " + port);

    // Define the routes for HTTP requests. Both GET and POST go to the same
    // one here...
    Router router = Router.router(vertx);
    router.get("/hello").handler(this::get_handle);
    router.post("/hello").handler(this::post_handle);

    // And start listening
    vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(port, result -> {
              if (result.succeeded()) {
                logger.debug("Hello: Succeeded in starting the listener");
                fut.complete();
              } else {
                logger.error("Hello failed to start the listener: " + result.cause());
                fut.fail(result.cause());
              }
            });
  }

  // Handler for the GET requests.
  // Just replies "Hello, world" in plain text
  public void get_handle(RoutingContext ctx) {
    logger.debug("Hello: handling a GET request");
    responseText(ctx, 200).end("Hello, world\n");
  }

  // Handler for the POST request
  // Replies with a JSON structure that contains all posted data
  // As long as the input data is valid JSON, the output should be too.
  public void post_handle(RoutingContext ctx) {
    logger.debug("Hello: handling a POST request");
    String contentType = ctx.request().getHeader("Content-Type");
    if (contentType != null && contentType.compareTo("application/json") != 0) {
      responseError(ctx, 400, "Only accepts Content-Type application/json");
    } else {
      responseJson(ctx, 200);
      ctx.response().setChunked(true);
      ctx.response().write("{ \"greeting\": \"Hello, world\",\n \"data\" : ");
      ctx.request().handler(x -> { // Pass the request body into the response, as it comes along
        ctx.response().write(x);
      });
      ctx.request().endHandler(x -> { // At the end of the body, close the structure
        ctx.response().end("\n}\n"); // and end the response processing
      });
    }
  }
}
