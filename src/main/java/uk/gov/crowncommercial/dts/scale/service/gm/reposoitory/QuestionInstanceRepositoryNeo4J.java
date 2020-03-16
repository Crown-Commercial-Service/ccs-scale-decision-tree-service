/**
 *
 * QuestionInstanceRepositoryNeo4J.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import java.util.Optional;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;

/**
 *
 */
@Repository
public interface QuestionInstanceRepositoryNeo4J extends Neo4jRepository<QuestionInstance, Long> {

  @Depth(2)
  Optional<QuestionInstance> findByUuid(String uuid);

}
