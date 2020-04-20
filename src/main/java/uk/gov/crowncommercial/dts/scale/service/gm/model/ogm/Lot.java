package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResultType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.RouteToMarket;

/**
 * Commercial agreement Lot (e.g. Linen and Laundry Services Lot 1b)
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Lot implements QuestionInstanceOutcome {

  String uuid;
  String agreementName;
  String lotName;
  String agreementDescription;
  String lotDescription;
  String agreementId;
  String url;
  JourneyResultType type;
  RouteToMarket routeToMarket;
  boolean scale;

}
