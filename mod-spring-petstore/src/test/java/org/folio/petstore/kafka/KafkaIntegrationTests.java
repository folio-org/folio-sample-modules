package org.folio.petstore.kafka;

import org.folio.petstore.ModPetstoreApplication;
import org.folio.petstore.domain.kafka.event.PetEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = ModPetstoreApplication.class)
@Testcontainers
public class KafkaIntegrationTests {

  @Container
  private static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

  private final String topicName = "spring-petStore";

  @Autowired
  private KafkaTemplate<String, PetEvent> petEventKafkaTemplate;

  @DynamicPropertySource
  public static void overrideProperties(DynamicPropertyRegistry registry) {

    registry.add("spring.kafka.bootstrap-servers",kafka::getBootstrapServers);
  }

  @Test
  public void testPetProoducerConsumer() {
    PetEvent petEvent = PetEvent.builder().name("test pet").tag("test animal").build();

    petEventKafkaTemplate.send(topicName,petEvent);

    petEventKafkaTemplate.flush();
  }

}
