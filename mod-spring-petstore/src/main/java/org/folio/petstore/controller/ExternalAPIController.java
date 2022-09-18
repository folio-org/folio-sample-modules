package org.folio.petstore.controller;


import lombok.RequiredArgsConstructor;
import org.folio.petstore.domain.service.impl.PetServiceImpl;
import org.folio.petstore.rest.resource.ExternalApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ExternalAPIController implements ExternalApi {

  private final PetServiceImpl petService;

  @Override
  @GetMapping("/random-user")
  public ResponseEntity<Object> getUser() {
    return ResponseEntity.ok(petService.getUser());
  }

  @Override
  @PostMapping("/create-user")
  public ResponseEntity<Object> createUser(@RequestBody Object user) {
    return ResponseEntity.ok(petService.createUser(user));
  }
}

