package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.lang.Nullable;
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
@RelationshipEntity(type = "HAS_OUTCOME")
public class HasOutcome {

  Long id;

  @Nullable
  Long lowerBoundInclusive;

  @Nullable
  Long upperBoundExclusive;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @StartNode
  AnswerGroup answerGroup;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  @EndNode
  QuestionInstanceOutcome questionInstanceOutcome;

}
