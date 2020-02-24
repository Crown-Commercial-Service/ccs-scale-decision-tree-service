/**
 *
 * GuidedMatchResult.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import org.neo4j.ogm.annotation.NodeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * GM Journey result
 */
@Data
@NodeEntity
public class JourneyResult implements AnswerOutcome {

  @JsonIgnore
  Long id;
  String name;
  String description;
  String agreementId;
  String url;

  public JourneyResult() {/* Required by Neo4J OGM */}

}
