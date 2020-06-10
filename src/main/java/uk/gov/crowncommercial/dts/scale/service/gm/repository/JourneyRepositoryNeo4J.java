package uk.gov.crowncommercial.dts.scale.service.gm.repository;

import java.util.Optional;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Journey;

@Repository
public interface JourneyRepositoryNeo4J extends Neo4jRepository<Journey, Long> {

  @Depth(1)
  Optional<Journey> findByUuid(String journeyUuid);

}
