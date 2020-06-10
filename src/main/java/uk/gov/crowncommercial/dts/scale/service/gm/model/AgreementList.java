package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Agreement;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstanceOutcome;

/**
 * Agreement collection wrapper
 */
public class AgreementList extends ArrayList<Agreement> implements OutcomeData {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AgreementList(final Collection<Agreement> agreements) {
    super(agreements);
  }

  /**
   * Returns a new populated instance from a collection of Lot super types, e.g
   * {@link QuestionInstanceOutcome}
   *
   * @param agreements
   * @return a new instance contain
   */
  public static AgreementList fromItems(final Collection<? super Agreement> agreements) {
    return new AgreementList(
        agreements.stream().map(i -> (Agreement) i).collect(Collectors.toSet()));
  }

}
