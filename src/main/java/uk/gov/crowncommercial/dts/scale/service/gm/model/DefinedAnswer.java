package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Builder;
import lombok.Value;

/**
 * Defined answer (for radio/checkbox selection)
 */
@Value
@Builder
public class DefinedAnswer {

  String uuid;
  String text;
  String hint;
  int order;

}
