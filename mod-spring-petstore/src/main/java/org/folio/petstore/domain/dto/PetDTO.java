package org.folio.petstore.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.folio.petstore.domain.entity.Pet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {
  private long id;
  private String name;
  private String tag;
}
