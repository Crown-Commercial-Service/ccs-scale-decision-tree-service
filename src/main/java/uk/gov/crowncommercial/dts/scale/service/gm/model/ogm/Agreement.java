package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.Data;
import uk.gov.crowncommercial.dts.scale.service.gm.model.OutcomeData;

/**
 * GM Journey result
 */
@Data
@NodeEntity
public class Agreement implements QuestionInstanceOutcome, OutcomeData {

  String uuid;
  String name;
  String description;
  String agreementId;
  String url;

  public Agreement() {/* Required by Neo4J OGM */}

}
