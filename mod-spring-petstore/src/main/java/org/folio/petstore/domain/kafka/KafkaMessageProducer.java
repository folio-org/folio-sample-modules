package org.folio.petstore.domain.kafka;

import lombok.RequiredArgsConstructor;
import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.kafka.event.PetEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {

  @Value("${application.kafka.topics[0].name}")
  private String topicName;

  private final KafkaTemplate<String, PetEvent> petEventKafkaTemplate;

  public void producePet(PetDTO petDTO){

    PetEvent petEvent = PetEvent.builder()
      .name(petDTO.getName())
      .tag(petDTO.getTag())
      .build();

    petEventKafkaTemplate.send(topicName, petEvent);

  }
}
