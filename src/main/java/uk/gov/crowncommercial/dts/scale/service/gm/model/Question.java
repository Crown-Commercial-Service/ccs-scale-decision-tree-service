/**
 *
 * Question.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.Set;
import lombok.Value;

/**
 * Guided Match Question
 */
@Value
public class Question {

  long id;
  String text;
  UIComponentType uiType;
  Set<Answer> answers;

}
