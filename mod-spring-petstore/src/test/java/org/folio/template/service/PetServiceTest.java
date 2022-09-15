package org.folio.template.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.folio.petstore.client.CreateUserClient;
import org.folio.petstore.client.UserClient;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
  @Mock
  private PetRepository petRepository;

  @Mock
  private PetMapper mapper;

  @Mock
  private UserClient userClient;

  @Mock
  private CreateUserClient createUserClient;

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
  public void shouldUpdatePet() {
    // Given
    PetDTO petDTO = new PetDTO();
    petDTO.setId(1L);
    petDTO.setName("Fluffy");
    petDTO.setTag("husky");

    Pet pet = new Pet(1, "Fluffy", "husky");

    // When
    when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
    when(petRepository.save(pet)).thenReturn(pet);
    when(mapper.toPetEntity(petDTO)).thenReturn(pet);
    when(mapper.toPetDTO(pet)).thenReturn(petDTO);

    // Then
    assertEquals(petDTO, petService.updatePet(petDTO));
    verify(petRepository, times(1)).save(pet);
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

  @Test
  public void shouldGetRandomUser() {
    // Given
    LinkedHashMap<String,String> data = new LinkedHashMap<>();
    data.put("email", "james@bond.com");
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode user = objectMapper.convertValue(data, JsonNode.class);

    // When
    when(userClient.getRandomUser()).thenReturn(user);

    // Then
    assertEquals(user, petService.getUser());
    verify(userClient, times(1)).getRandomUser();
  }

  @Test
  public void shouldCreateUser() {
    // Given
    LinkedHashMap<String,String> data = new LinkedHashMap<>();
    data.put("name", "morpheus");
    data.put("job", "leader");
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode user = objectMapper.convertValue(data, JsonNode.class);

    // When
    when(createUserClient.createUser(user)).thenReturn(user);

    // Then
    assertEquals(user, petService.createUser(user));
    verify(createUserClient, times(1)).createUser(user);
  }
}
