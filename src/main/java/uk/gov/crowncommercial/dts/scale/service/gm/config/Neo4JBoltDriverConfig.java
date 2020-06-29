package uk.gov.crowncommercial.dts.scale.service.gm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Configures the Neo4J Bolt driver with liveness check timeout (default to always test connection).
 * Not yet integrated into main Spring Data Neo4J config. See
 * https://docs.spring.io/spring-data/neo4j/docs/current-SNAPSHOT/reference/html/#reference:configuration:driver:connection-test
 */
@Configuration
@Slf4j
public class Neo4JBoltDriverConfig {

  @Bean
  public org.neo4j.ogm.config.Configuration configureBoltDriver(
      @Value("${spring.data.neo4j.uri}") final String boltUri,
      @Value("${spring.data.neo4j.username}") final String username,
      @Value("${spring.data.neo4j.password}") final String password,
      @Value("${neo4j.connection.liveness.check.timeout:0}") final int livenessCheckTimeout) {

    log.debug("Configuring Neo4J Bolt driver with URI: {}", boltUri);

    return new org.neo4j.ogm.config.Configuration.Builder()
        .connectionLivenessCheckTimeout(livenessCheckTimeout).verifyConnection(Boolean.TRUE)
        .uri(boltUri).credentials(username, password).build();
  }

}
