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
package org.folio.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 *
 */
public class MainVerticle extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger("folio-sample");

  @Override
  public void start(Future<Void> fut) throws IOException {
    Router router = Router.router(vertx);

    final int port = Integer.parseInt(System.getProperty("port", "8080"));
    logger.info("Starting hello " + ManagementFactory.getRuntimeMXBean().getName() + " on port " + port);

    // Define the routes for HTTP requests. Both GET and POST go to the same
    // one here...
    router.get("/hello").handler(this::get_handle);
    router.post("/hello").handler(this::post_handle);

    // And start listening
    vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(
                    port,
                    result -> {
                      if (result.succeeded()) {
                        fut.complete();
                      } else {
                        logger.error("sample failed: " + result.cause());
                        fut.fail(result.cause());
                      }
                    }
            );
  }

  // Handler for the GET requests. 
  // Just replies "Hello, World" in plain text
  public void get_handle(RoutingContext ctx) {
    ctx.response().setStatusCode(200);
    ctx.response().putHeader("Content-Type", "text/plain");

    ctx.request().endHandler(x -> {
      ctx.response().end("Hello, world\n");
    });
  }

  // Handler for the POST request
  // Replies with a Json structure that contains all posted data
  //
  public void post_handle(RoutingContext ctx) {
    ctx.response().setStatusCode(200);
    String contentType = ctx.request().getHeader("Content-Type");
    if ( contentType != null )
      ctx.response().putHeader("Content-Type", contentType);

    ctx.response().setChunked(true);
    ctx.response().write("{ \"greeting\": \"Hello, world\",\n \"data\" : ");
    ctx.request().handler(x -> { // Pass the body into the response, as it comes along
      ctx.response().write(x);
    });
    ctx.request().endHandler(x -> { // At the end of the body, close the structure
      ctx.response().end("\n}\n");
    });
  }

}
