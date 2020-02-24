/**
 *
 * QuestionRepositoryNeo4J.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;

/**
 *
 */
@Repository
public interface QuestionRepositoryNeo4J extends Neo4jRepository<Question, Long> {

  @Query("MATCH (currentQstn:Question)-[ans:ANSWER]->(nextQstn:Question) "
      + "WHERE ID(currentQstn) = $currentQstnId AND ID(ans) = $answerId RETURN ID(nextQstn)")
  Long getNextQuestionId(@Param("currentQstnId") long currentQstnId,
      @Param("answerId") long answerId);

}
