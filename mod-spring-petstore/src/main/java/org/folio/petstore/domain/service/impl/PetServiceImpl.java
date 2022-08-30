package org.folio.petstore.domain.service.impl;

import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.entity.Pet;
import org.folio.petstore.domain.service.PetService;
import org.folio.petstore.mapper.PetMapper;
import org.folio.petstore.repository.PetRepository;
import org.folio.spring.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {
  private final PetRepository petRepository;

  private final PetMapper mapper;

  public PetServiceImpl(PetRepository petRepository, PetMapper mapper) {
    this.petRepository = petRepository;
    this.mapper = mapper;
  }

  @Override
  public void createPet(PetDTO petDTO) {
    Pet pet = mapper.toPetEntity(petDTO);
    petRepository.save(pet);
  }

  @Override
  public List<PetDTO> listPetDTOs(Integer limit) {
    Pageable pageable = Pageable.ofSize(limit);
    List<PetDTO> petDTOList = mapper.toPetDTOList(petRepository.findAll(pageable));
    return null;
  }

  @Override
  public PetDTO getPetDTOById(String petId) {
    return mapper.toPetDTO(petRepository.findById(Long.valueOf(petId)).orElseThrow(()->new NotFoundException("Pet not found")));
  }

  @Override
  public void deletePetById(String petId) {
      petRepository.deleteById(Long.valueOf(petId));
  }
}