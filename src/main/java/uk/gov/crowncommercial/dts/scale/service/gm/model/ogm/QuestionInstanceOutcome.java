/**
 *
 * QuestionInstanceOutcome.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * An answer relationship can have multiple outcome types. OGM requires this to be mapped via a
 * common interface or base class.
 *
 * In this PoC case, <code>@NodeEntity</code> types {@link QuestionInstance} and {@link Agreement}
 * are the two possible questionInstanceOutcomes of answering a questionInstance.
 */
@NodeEntity("Outcome")
public interface QuestionInstanceOutcome {
  /* Marker interface */

  String getUuid();

}
