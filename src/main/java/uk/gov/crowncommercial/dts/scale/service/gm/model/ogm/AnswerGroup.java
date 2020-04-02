package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import lombok.Data;

/**
 * Guided Match AnswerGroup
 */
@Data
@NodeEntity
public class AnswerGroup {

  Long id;
  String name;

  @Relationship(type = "HAS_ANSWER", direction = Relationship.OUTGOING)
  Set<Answer> answers;

  @Relationship(type = "HAS_OUTCOME", direction = Relationship.OUTGOING)
  Set<QuestionInstanceOutcome> questionInstanceOutcomes;

  public AnswerGroup() {/* Required by Neo4J OGM */}

}
