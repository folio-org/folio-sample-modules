package org.folio.petstore.client.config;

import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;

public class PetStoreFeignClientConfig {
    @Bean
    public Client feignClient(okhttp3.OkHttpClient okHttpClient) {
      return new OkHttpClient(okHttpClient);
    }
}
