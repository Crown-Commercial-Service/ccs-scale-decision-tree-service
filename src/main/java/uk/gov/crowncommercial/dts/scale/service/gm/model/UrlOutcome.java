package uk.gov.crowncommercial.dts.scale.service.gm.model;

import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Agreement;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Url;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Url wrapper
 */
public class UrlOutcome extends ArrayList<Url> implements OutcomeData {

  private static final long serialVersionUID = 1L;

  public UrlOutcome(final Collection<Url> urls) {
    super(urls);
  }

  public static UrlOutcome getData(Url url) {
    ArrayList<Url> urlList = new ArrayList<Url>(Collections.singletonList(url));
    return new UrlOutcome(urlList);
  }

}
