/**
 *
 * Question.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.Set;
import lombok.Builder;
import lombok.Value;

/**
 *
 */
@Value
@Builder
public class Question implements OutcomeData {

  String uuid;
  String text;
  String hint;
  String pattern;
  QuestionType type;
  Set<DefinedAnswer> definedAnswers;

}
