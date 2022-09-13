package org.folio.petstore.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("application.kafka")
public class PetKafkaProperties {

  private Map<String, KafkaListenerProperties> listener;

  private List<KafkaTopic> topics;

  @Data
  public static class KafkaListenerProperties {

    private Integer concurrency;

    private String groupId;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class KafkaTopic {

    private String name;

    private Integer numPartitions;

    private Short replicationFactor;
  }

}
