package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Value;

/**
 *
 */
@Value
public class ConditionalInput {

  String text;
  String hint;

  /**
   * Only {@link QuestionType#TEXT_INPUT}, {@link QuestionType#NUMBER}, {@link QuestionType#DATE} or
   * {@link QuestionType#DATE_RANGE} are valid
   *
   * TODO: Implement some validation possibly utilising
   * <code>EnumSet.of(QuestionType.TEXT_INPUT ...)</code>
   */
  QuestionType type;

  Unit unit;

}
