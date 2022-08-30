package org.folio.petstore.controller;

import lombok.RequiredArgsConstructor;
import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.service.impl.PetServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1")
@RequiredArgsConstructor
public class PetsController {
  private PetServiceImpl petService;

  @PostMapping("/pets")
  public ResponseEntity<Void> createPet(@RequestBody PetDTO petDTO) {
    petService.createPet(petDTO);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/pets")
  public ResponseEntity<List<PetDTO>> showPetsList() {
    return ResponseEntity.ok(petService.listPetDTOs(10));
  }

  @GetMapping("/pets/{petId}")
  public ResponseEntity<PetDTO> getPetById(@PathVariable String petId) {
    return ResponseEntity.ok(petService.getPetDTOById(petId));
  }

  @DeleteMapping("/pets/{petId}")
  public ResponseEntity<Void> deletePetById(@PathVariable String petId) {
    petService.deletePetById(petId);
    return ResponseEntity.ok().build();
  }
}
