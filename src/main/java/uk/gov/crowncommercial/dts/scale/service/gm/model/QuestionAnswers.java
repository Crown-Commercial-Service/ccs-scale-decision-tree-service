package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.NonNull;
import lombok.Value;

/**
 * Container for client-provided answers
 */
@Value
public class QuestionAnswers {

  @NonNull
  GivenAnswer[] data;

}
