/**
 *
 * Answer.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Guided Match Answer
 */
@Data
@RelationshipEntity(type = "ANSWER")
public class Answer {

  Long id;
  String text;
  int displayOrder;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @StartNode
  Question question;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @EndNode
  AnswerOutcome outcome;

  public Answer() {/* Required by Neo4J OGM */}

}
