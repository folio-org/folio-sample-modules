package org.folio.petstore.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("application.kafka")
public class PetKafkaProperties {

  private Map<String, KafkaListenerProperties> listener;

  @Data
  public static class KafkaListenerProperties {

    private Integer concurrency;

    private String groupId;
  }


}
