package org.folio.petstore.domain.service;

import org.folio.petstore.domain.dto.PetDTO;

import java.util.List;

public interface PetService {
  void createPet(PetDTO petDTO);

  List<PetDTO> listPetDTOs(Integer limit);

  PetDTO getPetDTOById(String petId);

  void deletePetById(String petId);
}
