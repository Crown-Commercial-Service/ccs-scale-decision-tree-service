/**
 *
 * Question.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.Set;
import lombok.Builder;
import lombok.Value;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;

/**
 * API representation of the combination of a {@link QuestionDefinition} and
 * {@link QuestionInstance}
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
