/**
 *
 * JourneyResultRepositoryNeo4J.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import static java.lang.String.format;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResponses;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResult;

/**
 * See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class JourneyResultRepositoryNeo4J {

  private final Session session;

  /**
   * Queries the graph for a journey result by traversing it based on the user's chosen 'path'
   * (ordered collection of question/answer IDs).
   *
   * TODO: Find a more elegant approach..
   *
   * @param journeyId
   * @param journeyResponses containing the question/answer ID pairs
   * @return the JourneyResult matching the dynamically generated Cypher query pattern, or null if
   *         no match found.
   */
  public JourneyResult getJourneyResult(final int journeyId,
      final JourneyResponses journeyResponses) {

    StringBuilder patternFragments = new StringBuilder();
    StringJoiner whereFragments =
        new StringJoiner(" AND ", " WHERE ", format(" AND ID(j) = %d ", journeyId));

    String start = "MATCH (j:Journey)-[:FIRST_QUESTION]->";

    // Answers (and possibly questions) may appear more than once in the pattern, so make their var
    // names unique
    AtomicInteger fragmentId = new AtomicInteger();
    journeyResponses.getResponses().forEach(jr -> {
      final int f = fragmentId.get();
      patternFragments.append(format("(q%d_%s:Question)-[a%d_%d:ANSWER]->", jr.getQuestionId(), f,
          jr.getAnswerId(), f));
      whereFragments.add(format("ID(q%d_%d) = %d AND ID(a%d_%d) = %d", jr.getQuestionId(), f,
          jr.getQuestionId(), jr.getAnswerId(), f, jr.getAnswerId()));
      fragmentId.incrementAndGet();
    });

    patternFragments.append("(jr:JourneyResult)");

    String returnFragment = "RETURN jr";
    String cypherQry = start + patternFragments + whereFragments + returnFragment;
    log.debug("Cypher query for JourneyResult: {}", cypherQry);

    return session.queryForObject(JourneyResult.class, cypherQry, Collections.emptyMap());
  }

}
