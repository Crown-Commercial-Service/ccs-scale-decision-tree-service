package uk.gov.crowncommercial.dts.scale.service.gm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Lot;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstanceOutcome;

/**
 * Lot collection wrapper
 */
public class LotList extends ArrayList<Lot> implements OutcomeData {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LotList(final Collection<Lot> lots) {
    super(lots);
  }

  /**
   * Returns a new populated instance from a collection of Lot super types, e.g
   * {@link QuestionInstanceOutcome}
   *
   * @param items
   * @return a new instance contain
   */
  public static LotList fromItems(final Collection<? super Lot> items) {
    return new LotList(items.stream().map(i -> (Lot) i).collect(Collectors.toSet()));
  }

}
