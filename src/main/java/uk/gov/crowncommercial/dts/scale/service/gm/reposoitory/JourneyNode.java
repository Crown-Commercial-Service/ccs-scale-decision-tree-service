package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Value;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResult;

/**
 * Rudimentary decision tree 'node' for GM journeys
 */
@Value
@Builder
public class JourneyNode {

  Optional<Integer> questionId;
  Optional<JourneyResult> result;

  /**
   * Map of answer IDs to next node (next question or journey result)
   */
  Optional<Map<Integer, JourneyNode>> answerProgressionNodes;

}
