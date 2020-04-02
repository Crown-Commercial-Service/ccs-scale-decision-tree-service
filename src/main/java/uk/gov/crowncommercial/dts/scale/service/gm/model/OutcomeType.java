package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerated outcome types
 */
public enum OutcomeType {
  QUESTION, AGREEMENT, SUPPORT;

  @JsonValue
  public String getName() {
    return this.name().toLowerCase();
  }
}