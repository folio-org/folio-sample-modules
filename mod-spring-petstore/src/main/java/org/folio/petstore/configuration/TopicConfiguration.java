package org.folio.petstore.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {
  @Autowired
  PetKafkaProperties kafkaProperties;

  @Bean
  public NewTopic generalTopic() {
    PetKafkaProperties.KafkaTopic topics = kafkaProperties.getTopics().get(0);

    return TopicBuilder.name(topics.getName())
      .partitions(topics.getNumPartitions())
      .replicas(topics.getReplicationFactor())
      .build();
  }
}
