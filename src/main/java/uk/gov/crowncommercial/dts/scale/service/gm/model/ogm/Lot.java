package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResultType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.OutcomeData;

/**
 * Commercial agreement Lot (e.g. Linen and Laundry Services Lot 1b)
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Lot implements QuestionInstanceOutcome, OutcomeData {

  String uuid;
  String name;
  String description;
  String agreementId;
  String url;
  JourneyResultType type;

}
