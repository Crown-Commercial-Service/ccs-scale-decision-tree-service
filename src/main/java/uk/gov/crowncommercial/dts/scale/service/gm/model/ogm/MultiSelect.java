package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.lang.Nullable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class MultiSelect {

  String uuid;
  String group;

  /**
   * When multiple answers are given and these answers span different multi-select groups, this
   * attribute governs which route takes precedence (1 = highest precedence)
   */
  @Nullable
  Integer mixPrecedence;

  boolean primary;

  @Relationship(type = "HAS_OUTCOME", direction = Relationship.OUTGOING)
  Set<HasOutcome> hasOutcomeRels;

  @Relationship(type = "HAS_OUTCOME", direction = Relationship.OUTGOING)
  Set<QuestionInstanceOutcome> questionInstanceOutcomes;
}
