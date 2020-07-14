package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Unit of value for numerical conditional input / question types
 */
public enum Unit {

  @JsonProperty("currency")
  CURRENCY,

  @JsonProperty("quantity")
  QUANTITY,

  @JsonProperty("days")
  DAYS,

  @JsonProperty("weeks")
  WEEKS,

  @JsonProperty("months")
  MONTHS,

  @JsonProperty("years")
  YEARS;

}
