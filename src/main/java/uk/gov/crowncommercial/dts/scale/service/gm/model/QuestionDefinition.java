package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;

/**
 * API representation of the combination of a {@link Question} (the definition) and
 * {@link QuestionInstance} (the usage in the graph)
 */
@Value
@Builder
public class QuestionDefinition {

  String uuid;
  String text;
  String hint;
  String pattern;
  QuestionType type;
  List<AnswerDefinition> answerDefinitions;

}
