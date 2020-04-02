package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Container for client-provided answers
 */
@JsonDeserialize(builder = QuestionAnswers.QuestionAnswersBuilder.class)
@Value
@Builder
public class QuestionAnswers {

  @NonNull
  String[] data;

  @JsonPOJOBuilder(withPrefix = "")
  public static class QuestionAnswersBuilder {
    // Enhanced by Lombok
  }

}
