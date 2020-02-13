package uk.gov.crowncommercial.dts.scale.service.gm.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * GM Journey value class
 *
 */
@Data
@NodeEntity
public class Journey {

  Long id;
  String name;
  String[] searchTerms;

  @JsonIgnore
  @Relationship(type = "FIRST_QUESTION", direction = Relationship.OUTGOING)
  Question question;

  public Journey() {/* Required by Neo4J OGM */}

  public long getQuestionId() {
    return question.getId();
  }
}
