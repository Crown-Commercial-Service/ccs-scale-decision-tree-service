package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * QuestionDefinition type
 *
 */
public enum QuestionType {

  @JsonProperty("boolean")
  BOOLEAN,

  @JsonProperty("list")
  LIST,

  @JsonProperty("multiSelect")
  MULTI_SELECT_LIST,

  @JsonProperty("number")
  NUMBER,

  @JsonProperty("textInput")
  TEXT_INPUT,

  @JsonProperty("date")
  DATE,

  @JsonProperty("dateRange")
  DATE_RANGE,

  @JsonProperty("postcode")
  POSTCODE,

  @JsonProperty("nuts")
  NUTS;
}
