package org.folio.petstore.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.folio.petstore.client.config.PetStoreFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "create-user", url="https://reqres.in", configuration = PetStoreFeignClientConfig.class)
public interface CreateUserClient {
  @PostMapping("/api/users")
  JsonNode createUser(@RequestBody Object user);
}
