package uk.gov.crowncommercial.dts.scale.service.gm.exception;

/**
 *
 */
public class AnswersValidationException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AnswersValidationException(final String msg) {
    super(msg);
  }

  public AnswersValidationException(final String msg, final Throwable t) {
    super(msg, t);
  }

}
