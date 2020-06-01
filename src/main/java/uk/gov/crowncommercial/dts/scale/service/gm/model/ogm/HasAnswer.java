package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RelationshipEntity(type = "HAS_ANSWER")
public class HasAnswer {

  Long id;
  Integer order;
  Boolean mutex;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @StartNode
  AnswerGroup answerGroup;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @EndNode
  Answer answer;

}
