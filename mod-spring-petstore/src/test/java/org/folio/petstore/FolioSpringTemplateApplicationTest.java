package org.folio.petstore;

import org.folio.spring.liquibase.FolioLiquibaseConfiguration;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.folio.tenant.rest.resource.TenantApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles({"test", "testcontainers-pg"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FolioSpringTemplateApplicationTest {

  @EnableAutoConfiguration(exclude = {FolioLiquibaseConfiguration.class})
  @RestController("folioTenantController")
  @Profile("test")
  static class TestTenantController implements TenantApi {

    @Override
    public ResponseEntity<Void> deleteTenant(String operationId) {
      return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> postTenant(@Valid TenantAttributes tenantAttributes) {
      return ResponseEntity.status(HttpStatus.CREATED).build();
    }

  }

  @Test
  void shouldAnswerWithTrue() {
    assertTrue(true);
  }

}
