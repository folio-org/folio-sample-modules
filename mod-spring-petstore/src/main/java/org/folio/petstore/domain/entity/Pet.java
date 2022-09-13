package org.folio.petstore.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "pets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
