package org.folio.petstore.domain.kafka.event;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetEvent {

  private final Date publishedDate = new Date();
  private String name;
  private String tag;
}
