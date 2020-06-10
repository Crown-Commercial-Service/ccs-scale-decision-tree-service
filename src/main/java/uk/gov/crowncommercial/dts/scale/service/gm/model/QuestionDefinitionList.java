package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Question definition collection wrapper
 */
public class QuestionDefinitionList extends ArrayList<QuestionDefinition> implements OutcomeData {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public QuestionDefinitionList(final Collection<QuestionDefinition> questionDefinitions) {
    super(questionDefinitions);
  }

  public static QuestionDefinitionList fromItems(final QuestionDefinition... questionDefinitions) {
    return new QuestionDefinitionList(Arrays.asList(questionDefinitions));
  }

}
