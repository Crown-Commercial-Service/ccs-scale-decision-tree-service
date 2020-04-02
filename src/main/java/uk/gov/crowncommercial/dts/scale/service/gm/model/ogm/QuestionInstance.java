package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import lombok.Data;

/**
 * Guided Match QuestionInstance
 */
@Data
@NodeEntity
public class QuestionInstance implements QuestionInstanceOutcome {

  Long id;
  String uuid;

  @Relationship(type = "HAS_ANSWER_GROUP", direction = Relationship.OUTGOING)
  Set<AnswerGroup> answerGroups;

  @Relationship(type = "DEFINED_BY", direction = Relationship.OUTGOING)
  QuestionDefinition questionDefinition;

  public QuestionInstance() {/* Required by Neo4J OGM */}
}
