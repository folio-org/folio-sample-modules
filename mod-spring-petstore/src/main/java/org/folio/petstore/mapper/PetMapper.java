package org.folio.petstore.mapper;

import org.folio.petstore.domain.dto.PetDTO;
import org.folio.petstore.domain.entity.Pet;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PetMapper {
  PetDTO toPetDTO(Pet pet);

  Pet toPetEntity(PetDTO petDto);

  List<PetDTO> toPetDTOList(Iterable<Pet> petList);
}
