/**
 *
 * Answer.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Value;

/**
 * Guided Match Answer
 */
@Value
public class Answer {

  long id;
  String text;
  int displayOrder;

}
