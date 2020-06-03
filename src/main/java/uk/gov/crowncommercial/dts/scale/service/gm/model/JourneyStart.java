package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Value;

/**
 * Journey with embedded first question
 */
@Value
public class JourneyStart {

  String uuid;
  String name;
  QuestionDefinition firstQuestion;

}
