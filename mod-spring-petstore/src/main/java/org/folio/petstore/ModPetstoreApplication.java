package org.folio.petstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ModPetstoreApplication {
  public static void main(String[] args) {
    SpringApplication.run(ModPetstoreApplication.class, args);
  }

}
