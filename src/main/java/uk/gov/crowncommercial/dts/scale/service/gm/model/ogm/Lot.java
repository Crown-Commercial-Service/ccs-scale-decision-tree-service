package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.Data;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResultType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.OutcomeData;

/**
 * Commercial agreement Lot (e.g. Linen and Laundry Services Lot 1b)
 */
@Data
@NodeEntity
public class Lot implements QuestionInstanceOutcome, OutcomeData {

  String uuid;
  String name;
  String description;
  String agreementId;
  String url;
  JourneyResultType type;

  public Lot() {/* Required by Neo4J OGM */}

}
