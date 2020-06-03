package uk.gov.crowncommercial.dts.scale.service.gm.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyStart;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.repository.JourneyRepositoryNeo4J;

/**
 * GM Journeys service component
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyService {

  private final JourneyRepositoryNeo4J journeyRepo;
  private final QuestionService questionService;

  public JourneyStart getJourney(final String journeyUuid) {
    Journey journey = journeyRepo.findByUuid(journeyUuid).orElseThrow(RuntimeException::new);
    QuestionDefinition firstQuestion =
        questionService.getQuestion(journey.getQuestionInstance().getUuid());

    log.debug("Journey: {}, first question: {}", journey, firstQuestion);

    return new JourneyStart(journey.getUuid(), journey.getName(), firstQuestion);
  }

}
