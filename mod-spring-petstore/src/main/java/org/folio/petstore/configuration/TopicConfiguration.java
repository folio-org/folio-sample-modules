package org.folio.petstore.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {

  @Value("${application.kafka.topics[0].name}")
  private String topicName;

  @Value("${application.kafka.topics[0].numPartitions}")
  private int numPartitions;

  @Value("${application.kafka.topics[0].replicationFactor}")
  private int replicationFactor;

  @Bean
  public NewTopic generalTopic() {
    return TopicBuilder.name(topicName)
      .partitions(numPartitions)
      .replicas(replicationFactor)
      .build();
  }
}
