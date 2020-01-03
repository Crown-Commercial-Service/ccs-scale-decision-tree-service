package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Value;

/**
 * GM Journey value class
 *
 */
@Value
public class Journey {
  long id;
  String name;
  String[] searchTerms;
  long questionId;
}
