package org.folio.hello;

import org.folio.hello.api.SumApi;
import org.folio.hello.model.InputIntegers;
import org.folio.hello.model.SumResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class SumController implements SumApi {

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<SumResponse> sumBodyGet(InputIntegers body) {
    SumResponse response = new SumResponse();
    response.setSum(body.getA() + body.getB());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /** {@inheritDoc} */
  @Override
  public ResponseEntity<SumResponse> sumQueryGet(Integer a, Integer b) {
    SumResponse response = new SumResponse();
    response.setSum(a + b);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
