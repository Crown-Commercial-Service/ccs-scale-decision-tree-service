package uk.gov.crowncommercial.dts.scale.service.gm.repository;

import java.util.List;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstanceOutcome;

/**
 * Repository for retrieving either
 */
@Repository
public interface OutcomeRepository extends Neo4jRepository<QuestionInstanceOutcome, Long> {

  @Query("MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(ag:AnswerGroup)-[:HAS_OUTCOME]->(outcome) "
      + "WHERE (ag)-[:HAS_ANSWER]->(:Answer {uuid: $answerUuid}) "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:QuestionDefinition) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer)  "
      + "RETURN outcome, r, qd, rnag, nag, na, a")
  List<QuestionInstanceOutcome> findSingleStaticAnswerOutcome(
      @Param("currentQstnUuid") String currentQstnUuid, @Param("answerUuid") String answerUuid);

  @Query("MATCH (a:Answer) WHERE a.uuid IN $answerUuids " + "WITH collect(a) as answers "
      + "MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(ag1:AnswerGroup)-[:MULTI_SELECT]->(mso) "
      + "WHERE all(a in answers WHERE (ag1)-[:HAS_ANSWER]->(a)) "
      + "OPTIONAL MATCH (mso)-[:HAS_OUTCOME]->(outcome) "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:QuestionDefinition) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer) "
      + "RETURN outcome, r, qd, rnag, nag, na, a")
  List<QuestionInstanceOutcome> findMultiStaticAnswerOutcome(
      @Param("currentQstnUuid") String currentQstnUuid, @Param("answerUuids") String[] answerUuids);

  @Query("MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(ag1:AnswerGroup)-[:MULTI_SELECT]->(outcome) "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:QuestionDefinition) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer) "
      + "RETURN outcome, r, qd, rnag, nag, na, a")
  List<QuestionInstanceOutcome> findMultiDynamicAnswerOutcome(
      @Param("currentQstnUuid") String currentQstnUuid);

  @Depth(2)
  List<QuestionInstanceOutcome> findByUuid(String uuid);

  @Query("MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(ag:AnswerGroup)-[ha:HAS_OUTCOME]->(outcome) "
      + "WHERE (ag)-[:HAS_ANSWER]->(:Answer {uuid: $answerUuid}) "
      + "AND ha.lowerBoundInclusive <= $answerValue AND ha.upperBoundExclusive > $answerValue "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:QuestionDefinition) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer)  "
      + "RETURN outcome, r, qd, rnag, nag, na, a")
  List<QuestionInstanceOutcome> findSingleStaticConditionalNumericAnswerOutcome(
      @Param("currentQstnUuid") String currentQstnUuid, @Param("answerUuid") String answerUuid,
      @Param("answerValue") String answerValue);

}
