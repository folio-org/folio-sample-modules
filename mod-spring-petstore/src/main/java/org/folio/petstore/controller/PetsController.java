package org.folio.petstore.controller;

import lombok.RequiredArgsConstructor;
import org.folio.petstore.domain.dto.Pet;
import org.folio.petstore.rest.resource.PetsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class PetsController implements PetsApi {

  private Map<Long, Pet> petStorage = new ConcurrentHashMap<>();

  @Override
  public ResponseEntity<Void> createPets() {
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Pet>> listPets(Integer limit) {
    return ResponseEntity.ok(new ArrayList<>(petStorage.values()));
  }

  @Override
  public ResponseEntity<Pet> showPetById(String petId) {
    return ResponseEntity.ok(petStorage.get(Long.valueOf(petId)));
  }

  @PostConstruct
  public void init() {
    var pet = new Pet();
    pet.setId(1L);
    pet.setName("Doggy Dog");
    pet.setTag("Lucky boy");

    petStorage.put(pet.getId(), pet);

    pet = new Pet();
    pet.setId(2L);
    pet.setName("Red Fox");
    pet.setTag("Cunning foxy");

    petStorage.put(pet.getId(), pet);

    pet = new Pet();
    pet.setId(3L);
    pet.setName("Colorful Parrot");
    pet.setTag("Rainbow bird");

    petStorage.put(pet.getId(), pet);
  }
}
