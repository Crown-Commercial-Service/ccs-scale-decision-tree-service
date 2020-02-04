/**
 *
 * GuidedMatchResult.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

import lombok.Value;

/**
 * GM Journey result
 */
@Value
public class JourneyResult {

  String name;
  String description;
  String agreementId;
  String url;

}
