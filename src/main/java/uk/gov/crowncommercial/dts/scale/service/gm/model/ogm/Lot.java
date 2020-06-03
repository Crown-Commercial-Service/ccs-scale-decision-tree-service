package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uk.gov.crowncommercial.dts.scale.service.gm.model.RouteToMarket;

/**
 * Commercial agreement Lot (e.g. Linen and Laundry Services Lot 1b)
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Lot {

  String number;

  /**
   * Primary RTM - {@link RouteToMarket#BAT} or {@link RouteToMarket#CAT}
   */
  RouteToMarket type;

  /**
   * Secondary RTM - {@link RouteToMarket#DA} or {@link RouteToMarket#FC}
   */
  RouteToMarket routeToMarket;
  String url;
  boolean scale;

}
