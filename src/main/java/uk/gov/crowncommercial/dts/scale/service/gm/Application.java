package uk.gov.crowncommercial.dts.scale.service.gm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * SpringBoot application entry point
 *
 */
@SpringBootApplication
@EnableNeo4jRepositories("uk.gov.crowncommercial.dts.scale.service.gm.reposoitory")
@EntityScan({"uk.gov.crowncommercial.dts.scale.service.gm.model"})
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
