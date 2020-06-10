package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Answer {

  String uuid;
  String text;
  String hint;
  String outcomeUuid;

  /*
   * Related to a Question (i.e. definition). There is no direct routing out of a conditional input
   * question in the graph, and by doing it this way (i.e. not via a QuestionInstance) we avoid the
   * OGM returning multiple questino instance outcomes when there should only be one (it returns the
   * actual outcome AND the conditional input question instance
   */
  @Relationship(type = "HAS_CONDITIONAL_INPUT", direction = Relationship.OUTGOING)
  Question conditionalInputQuestion;

  @Transient
  Integer order;

  @Transient
  boolean mutex;
}
