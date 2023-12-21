package org.folio.hello;

import org.folio.hello.api.HelloApi;
import org.folio.hello.model.HelloPost200Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main API controller
 */
@RestController
public final class HelloController implements HelloApi {

  public static final String GREETING = "Hello, world!\n";

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<String> helloGet() {
    return new ResponseEntity<>(GREETING, HttpStatus.OK);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<HelloPost200Response> helloPost(Object body) {
    var response = new HelloPost200Response();
    response.setGreeting(GREETING);
    response.setData(body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
