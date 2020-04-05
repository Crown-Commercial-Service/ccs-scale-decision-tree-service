package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.BOOLEAN;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.LIST;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.AnswersValidationException;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.OutcomeException;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Outcome;
import uk.gov.crowncommercial.dts.scale.service.gm.model.OutcomeType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionAnswers;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Lot;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Answer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstanceOutcome;
import uk.gov.crowncommercial.dts.scale.service.gm.repository.OutcomeRepository;
import uk.gov.crowncommercial.dts.scale.service.gm.repository.QuestionInstanceRepositoryNeo4J;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutcomeService {

  private final OutcomeRepository outcomeRepo;
  private final QuestionInstanceRepositoryNeo4J questionInstanceRepository;
  private final QuestionService questionService;
  private final LookupService lookupService;

  /**
   * Attempt to find an 'outcome' (a {@link QuestionInstanceOutcome} instance which is either a
   * {@link QuestionInstance} or an {@link Lot}) from firstly, the static answers defined in
   * the graph model itself, and secondly from the dynamic lookup source. The result is then wrapped
   * in a new {@link Outcome} object and returned, or an exception thrown.
   *
   * @param currentQstnUuid
   * @param answerUuid
   * @return an Outcome instance containing either a question instance or an agreement
   * @throws OutcomeException if no outcome is discovered
   */
  public Outcome getQuestionInstanceOutcome(final String currentQstnUuid,
      final QuestionAnswers questionAnswers) {

    QuestionInstance currentQuestionInstance =
        questionInstanceRepository.findByUuid(currentQstnUuid)
            .orElseThrow(() -> new RuntimeException("TODO: QuestionInstance not found etc"));

    // Validate the incoming answers against the current question type:
    validateQuestionAnswers(currentQuestionInstance, questionAnswers);

    QuestionType questionType = currentQuestionInstance.getQuestionDefinition().getType();
    Optional<QuestionInstanceOutcome> optOutcome;

    // Treat MULTI_SELECT questions as if they are LIST/BOOLEAN when only single answer selected:
    if (asList(BOOLEAN, LIST).contains(questionType)
        || (questionType.equals(QuestionType.MULTI_SELECT_LIST)
            && questionAnswers.getData().length == 1)) {

      optOutcome =
          outcomeRepo.findSingleStaticAnswerOutcome(currentQstnUuid, questionAnswers.getData()[0]);
      log.debug("Single answer outcome retrieval from graph via static answers: {}", optOutcome);

      if (!optOutcome.isPresent()) {
        Answer answer = lookupService.getAnswer(questionAnswers.getData()[0]);
        optOutcome = outcomeRepo.findByUuid(answer.getOutcomeUuid());
        log.debug("Single answer outcome retrieval from graph lookup service answers: {}",
            optOutcome);
      }
    } else if (questionType.equals(QuestionType.MULTI_SELECT_LIST)) {

      optOutcome =
          outcomeRepo.findMultiStaticAnswerOutcome(currentQstnUuid, questionAnswers.getData());
      log.debug("Multi answer outcome retrieval from graph via static answers: {}", optOutcome);

      if (!optOutcome.isPresent()) {
        optOutcome = outcomeRepo.findMultiDynamicAnswerOutcome(currentQstnUuid);
        log.debug("Multi answer outcome retrieval from graph (dynamic answers): {}", optOutcome);
      }
    } else {
      throw new AnswersValidationException("Question / answer type not currently supported");
    }

    if (optOutcome.isPresent()) {
      return resolveOutcome(optOutcome.get());
    }

    // Graph is malformed or lookup service does not contain outcome
    throw new OutcomeException(currentQstnUuid, questionAnswers.getData());
  }

  private Outcome resolveOutcome(final QuestionInstanceOutcome questionInstanceOutcome) {
    log.debug("Resolving Outcome for QuestionInstanceOutcome: {}", questionInstanceOutcome);
    if (questionInstanceOutcome instanceof QuestionInstance) {

      // TODO: Investigate why, when cast to a QuestionInstance the entity is not fully hydrated.
      // Refactor accordingly (should not be necessary to load same object from graph
      return new Outcome(OutcomeType.QUESTION,
          questionService.convertToQuestion(questionInstanceRepository
              .findById(((QuestionInstance) questionInstanceOutcome).getId(), 2).get()));
    } else if (questionInstanceOutcome instanceof Lot) {
      return new Outcome(OutcomeType.LOT, (Lot) questionInstanceOutcome);
    } else {
      throw new ClassCastException(
          "Unknown QuestionInstanceOutcome type: " + questionInstanceOutcome.getClass());
    }
  }

  private void validateQuestionAnswers(final QuestionInstance currentQuestionInstance,
      final QuestionAnswers questionAnswers) {

    log.debug("Validating answers: {} for question instance: {}",
        Arrays.toString(questionAnswers.getData()), currentQuestionInstance.getUuid());

    switch (currentQuestionInstance.getQuestionDefinition().getType()) {

      case BOOLEAN:
        if (questionAnswers.getData().length != 1) {
          throw new AnswersValidationException(
              "Question type 'boolean' expects single answer value");
        }
        validateUuids(questionAnswers.getData());
        break;
      case LIST:
        if (questionAnswers.getData().length != 1) {
          throw new AnswersValidationException("Question type 'list' expects single answer value");
        }
        validateUuids(questionAnswers.getData());
        break;
      case MULTI_SELECT_LIST:
        if (questionAnswers.getData().length < 1) {
          throw new AnswersValidationException(
              "Question type 'multiSelectList' expects one or more answer values");
        }
        validateUuids(questionAnswers.getData());
        break;
      case TEXT_INPUT:
        throw new NotImplementedException("TEXT_INPUT question type not implemented");
      case DATE:
        throw new NotImplementedException("DATE question type not implemented");
      case DATE_RANGE:
        throw new NotImplementedException("DATE_RANGE question type not implemented");
      case NUMBER:
        throw new NotImplementedException("NUMBER question type not implemented");
      case NUTS:
        throw new NotImplementedException("NUTS question type not implemented");
      case POSTCODE:
        throw new NotImplementedException("POSTCODE question type not implemented");
      default:
        throw new AnswersValidationException("Unable to validate unsupported QuestionType");
    }
  }

  private void validateUuids(final String[] answerValues) {
    for (String ans : answerValues) {
      try {
        UUID.fromString(ans);
      } catch (IllegalArgumentException ex) {
        throw new AnswersValidationException(
            format("Invalid UUID: %s, msg: %s", ans, ex.getMessage()));
      }
    }
  }

}
