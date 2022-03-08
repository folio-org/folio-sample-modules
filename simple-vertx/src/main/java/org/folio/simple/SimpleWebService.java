package org.folio.simple;

import static org.folio.okapi.common.HttpResponse.responseError;
import static org.folio.okapi.common.HttpResponse.responseJson;
import static org.folio.okapi.common.HttpResponse.responseText;

import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.okapi.common.OkapiClient;

/**
 *
 * @author heikki
 */
public class SimpleWebService {
  private static final Logger logger = LogManager.getLogger(SimpleWebService.class);
  private final HttpClient httpClient;

  public SimpleWebService(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  // Handler for the GET requests.
  // Calls the hello module, and reports its success
  public void get_handle(RoutingContext ctx) {
    logger.debug("Simple: Handling a GET request. About to call the hello module");
    OkapiClient okapiClient = new OkapiClient(httpClient, ctx);
    logger.debug("Contacting Okapi via " + okapiClient.getOkapiUrl());
    okapiClient.get("/hello")
      .onSuccess(body -> responseText(ctx, 200).end("Hello module says: '" + body + "'"))
      .onFailure(e -> responseError(ctx, 500, "Hello failed with " + e.getMessage()));
  }

  // Handler for the POST request
  // POSTs the same request to the hello module, and returns its response in a
  // new Json response
  public void post_handle(RoutingContext ctx) {
    if (! "application/json".equals(ctx.request().getHeader("Content-Type"))) {
      responseError(ctx, 400, "Only accepts Content-Type application/json");
      return;
    }
    String reqData = ctx.getBodyAsString();
    logger.debug("Simple: Received a POST of " + reqData);
    OkapiClient okapiClient = new OkapiClient(httpClient, ctx);
    okapiClient.setHeaders(Map.of(
        "Content-Type", "application/json",
        "X-Okapi-Tenant", ctx.request().getHeader("X-Okapi-Tenant")));
    okapiClient.post("/hello", reqData)
      .onSuccess(helloRes -> {
        JsonObject hjo = new JsonObject(helloRes);
        String greeting = hjo.getString("greeting");
        logger.info("Got a greeting from hello: " + greeting);
        hjo.put("simple", "Simple module did indeed call the hello module!");
        String simpleRes = hjo.encodePrettily();
        responseJson(ctx, 200).end(simpleRes);
      })
      .onFailure(e -> responseError(ctx, 500, "Hello failed with " + e.getMessage()));
  }

} // SimpleWebService
