package org.folio.petstore.domain.kafka;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.folio.petstore.domain.kafka.event.PetEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class KafkaMessageListner {

  @KafkaListener(
    topics = "${application.kafka.topics[0].name}",
    groupId = "${application.kafka.topics[0].groupId}",
    containerFactory = "kafkaListenerContainerFactory"
  )
  public void handlePetEvents(ConsumerRecord<String, PetEvent> consumerRecord){

    log.info("partition number -> {}",consumerRecord.partition());
    log.info("consumed message -> {}",consumerRecord.value().toString());
  }
}
