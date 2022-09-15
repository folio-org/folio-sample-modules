package org.folio.petstore.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.folio.petstore.client.config.PetStoreFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(name = "user", url="https://randomuser.me", configuration = PetStoreFeignClientConfig.class)
public interface UserClient {
  @GetMapping("/api/")
  JsonNode getRandomUser();
}
