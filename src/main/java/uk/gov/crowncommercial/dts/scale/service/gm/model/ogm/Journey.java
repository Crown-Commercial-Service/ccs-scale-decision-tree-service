package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

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

  String uuid;
  String name;
  String[] searchTerms;

  @JsonIgnore
  @Relationship(type = "FIRST_QUESTION", direction = Relationship.OUTGOING)
  QuestionInstance questionInstance;

  public Journey() {/* Required by Neo4J OGM */}

  public String getQuestionUuid() {
    return questionInstance.getUuid();
  }
}
