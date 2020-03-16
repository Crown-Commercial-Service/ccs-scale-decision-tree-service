/**
 *
 * QuestionInstanceOutcome.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import org.springframework.lang.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 *
 */
@Value
public class Outcome {

  OutcomeType outcomeType;

  @JsonProperty("data")
  @Nullable
  OutcomeData outcome;

}
