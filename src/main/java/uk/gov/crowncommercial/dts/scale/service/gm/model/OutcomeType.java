package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enumerated outcome types
 */
public enum OutcomeType {

  @JsonProperty("question")
  QUESTION,

  @JsonProperty("agreement")
  AGREEMENT,

  @JsonProperty("support")
  SUPPORT;
}
