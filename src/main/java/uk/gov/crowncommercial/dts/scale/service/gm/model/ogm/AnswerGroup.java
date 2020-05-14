package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Guided Match AnswerGroup
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class AnswerGroup {

  Long id;
  String name;

  /*
   * TODO: Investigate why it does not seem possible to hydrate both a
   * neighbours @RelationshipEntity and @NodeEntity
   */
  @Relationship(type = "HAS_MULTI_SELECT", direction = Relationship.OUTGOING)
  Set<MultiSelect> multiSelects = new HashSet<>();

  @Relationship(type = "HAS_ANSWER", direction = Relationship.OUTGOING)
  Set<HasAnswer> hasAnswerRels;

  @Relationship(type = "HAS_OUTCOME", direction = Relationship.OUTGOING)
  Set<HasOutcome> hasOutcomeRels;

}
