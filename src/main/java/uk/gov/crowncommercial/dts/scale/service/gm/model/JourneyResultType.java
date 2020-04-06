package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Journey result types - one of two route to market types (<code>BAT, CAT</code>),
 * <code>OTHER</code> to direct clients to general facilities marketplace(s) and
 * <code>SUPPORT</code> to provide details of how to contact CCS support team
 */
public enum JourneyResultType {

  BAT, CAT, OTHER, SUPPORT;

  @JsonValue
  public String getName() {
    return this.name().toLowerCase();
  }

}
