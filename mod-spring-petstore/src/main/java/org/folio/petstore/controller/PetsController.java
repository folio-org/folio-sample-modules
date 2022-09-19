package org.folio.petstore.controller;

import lombok.RequiredArgsConstructor;
import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.kafka.KafkaMessageProducer;
import org.folio.petstore.rest.resource.PetsApi;
import org.folio.petstore.domain.service.impl.PetServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PetsController implements PetsApi {

  private final PetServiceImpl petService;

  private final KafkaMessageProducer kafkaMessageProducer;

  @Override
  @PostMapping("/pets")
  public ResponseEntity<Void> createPet(@RequestBody PetDTO petDTO) {
    petService.createPet(petDTO);
    kafkaMessageProducer.producePet(petDTO);
    return ResponseEntity.ok().build();
  }

  @Override
  @PutMapping("/pets")
  public ResponseEntity<PetDTO> updatePet(@RequestBody PetDTO petDTO) {
    return ResponseEntity.ok(petService.updatePet(petDTO));
  }

  @Override
  @GetMapping("/pets")
  public ResponseEntity<List<PetDTO>> listPets(@RequestParam Integer limit) {
    return ResponseEntity.ok(petService.listPetDTOs(limit));
  }

  @Override
  @GetMapping("/pets/{petId}")
  public ResponseEntity<PetDTO> showPetById(@PathVariable String petId) {
    return ResponseEntity.ok(petService.getPetDTOById(petId));
  }

  @Override
  @DeleteMapping("/pets/{petId}")
  public ResponseEntity<Void> deletePetById(@PathVariable String petId) {
    petService.deletePetById(petId);
    return ResponseEntity.ok().build();
  }
}
