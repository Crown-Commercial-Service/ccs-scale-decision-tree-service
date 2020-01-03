/**
 *
 * JourneyService.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Collection;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResponses;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResult;
import uk.gov.crowncommercial.dts.scale.service.gm.reposoitory.JourneyRepository;

/**
 * GM Journeys service component
 */
@Service
@RequiredArgsConstructor
public class JourneyService {

  private final JourneyRepository journeyRepo;

  public Collection<Journey> searchJourneys(final String searchTerm) {
    return journeyRepo.searchJourneys(searchTerm);
  }

  public JourneyResult getJourneyResult(final int journeyId,
      final JourneyResponses journeyResponses) {
    return journeyRepo.getJourneyResult(journeyId, journeyResponses);
  }

}
