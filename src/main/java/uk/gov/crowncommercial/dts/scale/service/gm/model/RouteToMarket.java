package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Route to market primary ({@link #BAT} or {@link #CAT} and sub-types ({@link #DA} or {@link #FC})
 */
public enum RouteToMarket {

  /**
   * Buy-a-Thing
   */
  @JsonProperty("bat")
  BAT,

  /**
   * Contract-a-Thing
   */
  @JsonProperty("cat")
  CAT,

  /**
   * Direct Award
   */
  @JsonProperty("da")
  DA,

  /**
   * Further Competition
   */
  @JsonProperty("fc")
  FC;

}
