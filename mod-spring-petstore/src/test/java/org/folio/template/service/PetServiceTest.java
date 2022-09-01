package org.folio.template.service;

import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.entity.Pet;
import org.folio.petstore.domain.service.impl.PetServiceImpl;
import org.folio.petstore.mapper.PetMapper;
import org.folio.petstore.repository.PetRepository;
import org.folio.spring.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
  @Mock
  private PetRepository petRepository;

  @Mock
  private PetMapper mapper;

  @InjectMocks
  private PetServiceImpl petService;

  @Test
  public void shouldCreatePet() {
    // Given
    PetDTO petDTO = new PetDTO();
    petDTO.setName("Fluffy");
    petDTO.setTag("husky");

    Pet pet = new Pet(1, "Fluffy", "Husky");

    // When
    when(petRepository.save(pet)).thenReturn(pet);
    when(mapper.toPetEntity(petDTO)).thenReturn(pet);

    // Then
    petService.createPet(petDTO);
    assertEquals(pet, petRepository.save(pet));
    verify(petRepository, times(2)).save(pet);
  }

  @Test
  public void shouldGetPetList() {
    // Given
    List<Pet> pet = new ArrayList<>();
    List<PetDTO> petList = new ArrayList<>();
    Pageable pageable = Pageable.ofSize(10);
    Page<Pet> petPage = new PageImpl<>(pet);

    // When
    when(petRepository.findAll(pageable)).thenReturn(petPage);
    when(mapper.toPetDTOList(petPage)).thenReturn(petList);

    // Then
    assertEquals(petList, petService.listPetDTOs(10));
    verify(petRepository, times(1)).findAll(pageable);
  }

  @Test
  public void shouldGetPetById() {
    // Given
    PetDTO petDTO = new PetDTO();
    petDTO.setName("Fluffy");
    petDTO.setTag("husky");

    Pet pet = new Pet(1, "Fluffy", "Husky");

    // When
    when(petRepository.findById(Long.valueOf("1"))).thenReturn(Optional.of(pet));
    when(mapper.toPetDTO(pet)).thenReturn(petDTO);

    // Then
    assertEquals(petDTO, petService.getPetDTOById("1"));
    verify(petRepository, times(1)).findById(1L);
  }

  @Test
  public void shouldGetPetByIdThrowsException() {
    // Given
    PetDTO petDTO = new PetDTO();

    // When
    when(petRepository.findById(1L)).thenThrow(new NotFoundException("Pet not found"));

    // Then
    assertThrows(NotFoundException.class, () -> petService.getPetDTOById("1"));
    verify(petRepository, times(1)).findById(1L);
  }

  @Test
  public void shouldDeletePetById() {
    // When
    when(petRepository.existsById(1L)).thenReturn(true);
    doNothing().when(petRepository).deleteById(1L);

    // Then
    petService.deletePetById("1");
    verify(petRepository, times(1)).deleteById(1L);
  }
}
