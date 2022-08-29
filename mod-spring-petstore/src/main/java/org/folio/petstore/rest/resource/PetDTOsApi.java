package org.folio.petstore.rest.resource;

import org.folio.petstore.domain.dto.PetDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PetDTOsApi {
  public ResponseEntity<Void> createPetDTOs();
  public ResponseEntity<List<PetDTO>> listPetDTOs(Integer limit);
  public ResponseEntity<PetDTO> showPetDTOById(String petId);
}
