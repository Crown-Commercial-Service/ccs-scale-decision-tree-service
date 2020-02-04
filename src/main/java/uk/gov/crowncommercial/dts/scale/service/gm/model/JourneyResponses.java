package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * GM Journey responses, encapsulating an ordered list of {@link JourneyResponse} entities
 *
 */
@JsonDeserialize(builder = JourneyResponses.JourneyResponsesBuilder.class)
@Value
@Builder
public class JourneyResponses {

  /**
   * Ordered list of responses (TODO - check ordering imposed by client for PoC)
   */
  List<JourneyResponse> responses;

  @JsonPOJOBuilder(withPrefix = "")
  public static class JourneyResponsesBuilder {
    // Enhanced by Lombok
  }
}
