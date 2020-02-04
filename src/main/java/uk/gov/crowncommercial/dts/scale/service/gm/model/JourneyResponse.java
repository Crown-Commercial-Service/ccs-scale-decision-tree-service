/**
 *
 * JourneyResponse.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * Simple wrapper for a question ID - answer ID pair
 */
@JsonDeserialize(builder = JourneyResponse.JourneyResponseBuilder.class)
@Value
@Builder
public class JourneyResponse {

  int questionId;
  int answerId;

  @JsonPOJOBuilder(withPrefix = "")
  public static class JourneyResponseBuilder {
    // Enhanced by Lombok
  }

}
