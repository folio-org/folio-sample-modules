package org.folio.petstore.client.config;

import feign.RequestInterceptor;
import org.folio.spring.FolioExecutionContext;
import org.springframework.context.annotation.Bean;

public class PetStoreFeignClientConfig {
  @Bean
  public RequestInterceptor requestInterceptor(FolioExecutionContext folioExecutionContext) {
    return new FolioRequestInterceptor(folioExecutionContext);
  }
}
