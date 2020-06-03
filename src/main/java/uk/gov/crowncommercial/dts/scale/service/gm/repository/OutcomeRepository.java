package uk.gov.crowncommercial.dts.scale.service.gm.repository;

import java.util.List;
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
      + "OPTIONAL MATCH (outcome:QuestionInstance)-[r:DEFINED_BY]->(qd:Question) "
      + "OPTIONAL MATCH (outcome:QuestionInstance)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (outcome:Agreement)-[rlot:HAS_LOT]->(lot:Lot) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer) "
      + "OPTIONAL MATCH (a)-[hci:HAS_CONDITIONAL_INPUT]->(ciqi:QuestionInstance)-[cidb:DEFINED_BY]->(ciq:Question) "
      + "RETURN outcome, r, qd, rnag, nag, na, a, hci, ciqi, cidb, ciq, rlot, lot")
  List<QuestionInstanceOutcome> findSingleAnswerOutcomes(
      @Param("currentQstnUuid") String currentQstnUuid, @Param("answerUuid") String answerUuid);

  @Query("MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(:AnswerGroup)-[:HAS_MULTI_SELECT]-(:MultiSelect {uuid: $multiSelectUuid})-[:HAS_OUTCOME]->(outcome) "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:Question) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer)  "
      + "OPTIONAL MATCH (a)-[hci:HAS_CONDITIONAL_INPUT]->(ciqi:QuestionInstance)-[cidb:DEFINED_BY]->(ciq:Question) "
      + "RETURN outcome, r, qd, rnag, nag, na, a, hci, ciqi, cidb, ciq")
  List<QuestionInstanceOutcome> findMultiAnswerOutcomes(
      @Param("currentQstnUuid") String currentQstnUuid,
      @Param("multiSelectUuid") String multiSelectUuid);

  // @Query("MATCH (q:QuestionInstance {uuid:
  // $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(:AnswerGroup)-[:HAS_MULTI_SELECT]-(:MultiSelect)-[:HAS_OUTCOME]->(outcome)
  // "
  // + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:Question) "
  // + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
  // + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer) "
  // + "OPTIONAL MATCH
  // (a)-[hci:HAS_CONDITIONAL_INPUT]->(ciqi:QuestionInstance)-[cidb:DEFINED_BY]->(ciq:Question) "
  // + "RETURN outcome, r, qd, rnag, nag, na, a, hci, ciqi, cidb, ciq")
  // List<QuestionInstanceOutcome> findMultiDynamicAnswerOutcomes(
  // @Param("currentQstnUuid") String currentQstnUuid);

  @Query("MATCH (q:QuestionInstance {uuid: $currentQstnUuid})-[:HAS_ANSWER_GROUP]->(ag:AnswerGroup)-[ha:HAS_OUTCOME]->(outcome) "
      + "WHERE (ag)-[:HAS_ANSWER]->(:Answer {uuid: $answerUuid}) "
      + "AND ha.lowerBoundInclusive <= $answerValue AND ha.upperBoundExclusive > $answerValue "
      + "OPTIONAL MATCH (outcome)-[r:DEFINED_BY]->(qd:Question) "
      + "OPTIONAL MATCH (outcome)-[rnag:HAS_ANSWER_GROUP]->(nag:AnswerGroup) "
      + "OPTIONAL MATCH (nag)-[na:HAS_ANSWER]->(a:Answer)  "
      + "RETURN outcome, r, qd, rnag, nag, na, a")
  List<QuestionInstanceOutcome> findSingleConditionalNumericAnswerOutcomes(
      @Param("currentQstnUuid") String currentQstnUuid, @Param("answerUuid") String answerUuid,
      @Param("answerValue") Double answerValue);

}
