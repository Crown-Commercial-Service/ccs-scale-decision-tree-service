package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.reposoitory.JourneyRepositoryNeo4J;

/**
 * GM Journeys service component
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyService {

  private final JourneyRepositoryNeo4J journeyRepo;

  public Collection<Journey> searchJourneys(final String searchTerm) {
    List<Journey> journeys = journeyRepo.findBySearchTermsContains(searchTerm);
    log.debug("Found {} journeys. {}", journeys.size(), journeys);

    return journeys;
  }

}
