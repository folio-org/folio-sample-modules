package org.folio.petstore.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.folio.petstore.domain.dto.PetDTO;

import java.util.List;

public interface PetService {
  void createPet(PetDTO petDTO);

  PetDTO updatePet(PetDTO petDTO);

  List<PetDTO> listPetDTOs(Integer limit);

  PetDTO getPetDTOById(String petId);

  void deletePetById(String petId);

  JsonNode getUser();

  JsonNode createUser(Object user);
}
