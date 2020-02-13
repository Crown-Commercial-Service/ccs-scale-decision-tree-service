/**
 *
 * Question.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import lombok.Data;

/**
 * Guided Match Question
 */
@Data
@NodeEntity
public class Question implements AnswerOutcome {

  Long id;
  String text;
  UIComponentType uiType;

  @Relationship(type = "ANSWER", direction = Relationship.OUTGOING)
  Set<Answer> answers;

  public Question() {/* Required by Neo4J OGM */}

}
