package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Guided Match QuestionInstance
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class QuestionInstance implements QuestionInstanceOutcome {

  Long id;
  String uuid;

  @Relationship(type = "HAS_ANSWER_GROUP", direction = Relationship.OUTGOING)
  Set<AnswerGroup> answerGroups;

  @Relationship(type = "DEFINED_BY", direction = Relationship.OUTGOING)
  Question question;

}
