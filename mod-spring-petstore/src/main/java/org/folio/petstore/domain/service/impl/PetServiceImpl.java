package org.folio.petstore.domain.service.impl;

import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.entity.Pet;
import org.folio.petstore.domain.exception.EntityNotFoundException;
import org.folio.petstore.domain.service.PetService;
import org.folio.petstore.mapper.PetMapper;
import org.folio.petstore.repository.PetRepository;
import org.folio.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PetServiceImpl implements PetService {
  private final PetRepository petRepository;

  private final PetMapper mapper;

  @Autowired
  public PetServiceImpl(PetRepository petRepository, PetMapper mapper) {
    this.petRepository = petRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public void createPet(PetDTO petDTO) {
    Pet pet = mapper.toPetEntity(petDTO);
    petRepository.save(pet);
  }

  @Override
  @Transactional
  public PetDTO updatePet(PetDTO petDTO) {
    Pet pet = petRepository.findById(petDTO.getId()).orElseThrow(()->new EntityNotFoundException("Pet not found"));
    pet.setName(petDTO.getName());
    pet.setTag(petDTO.getTag());
    pet = petRepository.save(mapper.toPetEntity(petDTO));
    return mapper.toPetDTO(pet);
  }

  @Override
  public List<PetDTO> listPetDTOs(Integer limit) {
    Pageable pageable = Pageable.ofSize(limit);
    List<PetDTO> petDTOList = mapper.toPetDTOList(petRepository.findAll(pageable));
    return petDTOList;
  }

  @Override
  public PetDTO getPetDTOById(String petId) {
    return mapper.toPetDTO(petRepository.findById(Long.valueOf(petId)).orElseThrow(()->new EntityNotFoundException("Pet not found")));
  }

  @Override
  @Transactional
  public void deletePetById(String petId) {
    if (petRepository.existsById(Long.valueOf(petId))) {
      petRepository.deleteById(Long.valueOf(petId));
    }
    else {
      throw new EntityNotFoundException("Pet not found");
    }
  }
}
