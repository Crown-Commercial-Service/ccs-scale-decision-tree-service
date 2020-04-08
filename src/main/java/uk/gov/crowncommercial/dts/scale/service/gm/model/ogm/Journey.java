package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * GM Journey value class
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Journey {

  String uuid;
  String name;
  String[] searchTerms;

  @JsonIgnore
  @Relationship(type = "FIRST_QUESTION", direction = Relationship.OUTGOING)
  QuestionInstance questionInstance;

  public String getQuestionUuid() {
    return questionInstance.getUuid();
  }
}
