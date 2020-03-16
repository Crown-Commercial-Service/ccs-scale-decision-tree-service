/**
 *
 * OutcomeException.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.exception;

/**
 *
 */
public class OutcomeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private static final String ERR_MSG_FMT_OUTCOME =
      "Unable to locate outcome node in graph via static or dynamic answer sources for current question-instance-UUID: %s, answer-uuid: %s";

  public OutcomeException(final String questionInstanceUuid, final String answerUuid) {
    super(String.format(ERR_MSG_FMT_OUTCOME, questionInstanceUuid, answerUuid));
  }

}
