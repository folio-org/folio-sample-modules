package org.folio.simple;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import static org.folio.okapi.common.HttpResponse.responseError;
import static org.folio.okapi.common.HttpResponse.responseJson;
import static org.folio.okapi.common.HttpResponse.responseText;
import org.folio.okapi.common.OkapiClient;

/**
 *
 * @author heikki
 */
public class SimpleWebService {
  private final Logger logger = LoggerFactory.getLogger("folio-simple");

  // Handler for the GET requests.
  // Calls the hello module, and reports its success
  public void get_handle(RoutingContext ctx) {
    logger.debug("Simple: Handling a GET request. About to call the hello module");
    OkapiClient ok = new OkapiClient(ctx);
    logger.debug("Contacting Okapi via " );
    ok.get("/hello", res-> {
      if ( res.succeeded()) {
        String message = "Hello module says: '" + res.result() + "'";
        responseText(ctx, 200).end(message);
      } else {
        String message = res.cause().getMessage();
        responseError(ctx, 500, "Hello failed with " + message);
      }
    });
  }

  // Handler for the POST request
  // POSTs the same request to the hello module, and returns its response in a
  // new Json response
  public void post_handle(RoutingContext ctx) {
    String contentType = ctx.request().getHeader("Content-Type");
    if (contentType != null && contentType.compareTo("application/json") != 0) {
      responseError(ctx, 400, "Only accepts Content-Type application/json");
    } else {
      OkapiClient ok = new OkapiClient(ctx);
      String reqData = ctx.getBodyAsString();
      logger.debug("Simple: Received a POST of " + reqData);
      ok.post("/hello", reqData, postres->{
        if (postres.succeeded()) {
          String helloRes = postres.result();
          JsonObject hjo = new JsonObject(helloRes);
          String greeting = hjo.getString("greeting");
          logger.info("Got a greeting from hello: " + greeting);
          hjo.put("simple", "Simple module did indeed call the hello module!");
          String simpleRes = hjo.encodePrettily();
          responseJson(ctx, 200).end(simpleRes);
        } else {
          responseError(ctx, 500, postres.cause().getMessage());
        }
      });
    }
  }

} // SimpleWebService
