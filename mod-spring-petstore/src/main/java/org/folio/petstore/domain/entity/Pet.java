package org.folio.petstore.domain.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "pets")
public class Pet {
  @Id
  @GeneratedValue
  @Column(updatable = false)
  private long id;

  @Column(name = "name")
  @NotNull
  @Size(min = 4, max = 100)
  private String name;

  @Column(name = "tag")
  @NotNull
  @Size(min = 4, max = 100)
  private String tag;
}
