package uk.gov.crowncommercial.dts.scale.service.gm.exception;

import java.util.Arrays;
import uk.gov.crowncommercial.dts.scale.service.gm.model.GivenAnswer;

/**
 *
 */
public class OutcomeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String ERR_MSG_FMT_OUTCOME =
      "Unable to locate outcome node in graph via static or dynamic answer sources for current question-instance-UUID: %s, answer-uuids: %s";

  public OutcomeException(final String questionInstanceUuid, final GivenAnswer[] answerUuids) {
    super(String.format(ERR_MSG_FMT_OUTCOME, questionInstanceUuid, Arrays.toString(answerUuids)));
  }

}
