package uk.gov.crowncommercial.dts.scale.service.gm.model;

import org.springframework.lang.Nullable;
import lombok.Value;

/**
 * Varying-type outcome (agreement, question, support)
 */
@Value
public class Outcome {

  OutcomeType outcomeType;

  @Nullable
  OutcomeData data;

}
