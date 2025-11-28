package org.folio.sample;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.folio.okapi.common.HttpResponse.*;

/**
 * The main verticle. This is the HTTP server that accepts incoming requests and
 * routes them to the relevant handlers.
 */
public class MainVerticle extends VerticleBase {

  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  @Override
  public Future<?> start() {

    var port = Integer.parseInt(System.getProperty("port", "8080"));
    LOGGER.info("Starting hello {} on port {}",
        ManagementFactory.getRuntimeMXBean().getName(),
        port);

    // Define the routes for HTTP requests.
    var router = Router.router(vertx);
    router.get("/hello").handler(this::getHandle);
    router.post("/hello").handler(this::postHandle);

    // And start listening
    return vertx.createHttpServer()
        .requestHandler(router)
        .listen(port)
        .onSuccess(x -> LOGGER.debug("Hello: Succeeded in starting the listener"))
        .onFailure(e -> LOGGER.error("Hello failed to start the listener: {}", e.getMessage(), e));
  }

  // Handler for the GET requests.
  // Just replies "Hello, world" in plain text
  public void getHandle(RoutingContext ctx) {
    LOGGER.debug("Hello: handling a GET request");
    responseText(ctx, 200).end("Hello, world\n");
  }

  // Handler for the POST request
  // Replies with a JSON structure that contains all posted data
  // As long as the input data is valid JSON, the output should be too.
  public void postHandle(RoutingContext ctx) {
    LOGGER.debug("Hello: handling a POST request");
    if (! "application/json".equals(ctx.request().getHeader("Content-Type"))) {
      responseError(ctx, 400, "Content-Type must be application/json");
      return;
    }

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
