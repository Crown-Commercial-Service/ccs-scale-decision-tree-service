package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Route to market (sub) types, currently for CaT (Contract-a-Thing) purchasing routes only
 */
public enum RouteToMarket {

  /**
   * Direct Award
   */
  DA,

  /**
   * Further Competition
   */
  FC;

  @JsonValue
  public String getName() {
    return this.name().toLowerCase();
  }

}
