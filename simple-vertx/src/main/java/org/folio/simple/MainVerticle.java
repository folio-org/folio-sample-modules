/*
 * Copyright (C) 2015 Index Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.folio.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import static org.folio.okapi.common.HttpResponse.*;
import org.folio.okapi.common.OkapiClient;

/**
 * The main verticle. This is the HTTP server that accepts incoming requests and
 * routes them to the relevant handlers.
 */
public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger("folio-simple");
  private final SimpleWebService simple = new SimpleWebService();
  
  @Override
  public void start(Future<Void> fut) throws IOException {

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
            .requestHandler(router::accept)
            .listen(port, result -> {
              if (result.succeeded()) {
                logger.debug("Succeeded in starting the listener for simple");
                fut.complete();
              } else {
                logger.error("simple failed: " + result.cause());
                fut.fail(result.cause());
              }
            });
  }


}
