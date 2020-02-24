/**
 *
 * AnswerOutcome.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model;

/**
 * An answer relationship can have multiple outcome types. OGM requires this to be mapped via a
 * common interface or base class.
 *
 * In this PoC case, <code>@NodeEntity</code> types {@link Question} and {@link JourneyResult} are
 * the two possible outcomes of answering a question.
 */
public interface AnswerOutcome {
  /* Marker interface */
}
