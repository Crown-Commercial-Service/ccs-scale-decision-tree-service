package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.BOOLEAN;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.LIST;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.AnswersValidationException;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.OutcomeException;
import uk.gov.crowncommercial.dts.scale.service.gm.model.*;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Answer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Lot;
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
   * {@link QuestionInstance} or an {@link Lot}) from firstly, the static answers defined in the
   * graph model itself, and secondly from the dynamic lookup source. The result is then wrapped in
   * a new {@link Outcome} object and returned, or an exception thrown.
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
    List<QuestionInstanceOutcome> optOutcome;

    // Treat MULTI_SELECT questions as if they are LIST/BOOLEAN when only single answer selected:
    if (asList(BOOLEAN, LIST).contains(questionType)
        || (questionType.equals(QuestionType.MULTI_SELECT_LIST)
            && questionAnswers.getData().length == 1)) {

      optOutcome = outcomeRepo.findSingleStaticAnswerOutcome(currentQstnUuid,
          questionAnswers.getData()[0].getUuid());
      log.debug("Single answer outcome retrieval from graph via static answers: {}", optOutcome);

      if (optOutcome.isEmpty()) {
        Answer answer = lookupService.getAnswer(questionAnswers.getData()[0].getUuid());
        optOutcome = outcomeRepo.findByUuid(answer.getOutcomeUuid());
        log.debug("Single answer outcome retrieval from graph lookup service answers: {}",
            optOutcome);
      }
    } else if (questionType.equals(QuestionType.MULTI_SELECT_LIST)) {

      optOutcome = outcomeRepo.findMultiStaticAnswerOutcome(currentQstnUuid,
          extractUuids(questionAnswers.getData()));
      log.debug("Multi answer outcome retrieval from graph via static answers: {}", optOutcome);

      if (optOutcome.isEmpty()) {
        optOutcome = outcomeRepo.findMultiDynamicAnswerOutcome(currentQstnUuid);
        log.debug("Multi answer outcome retrieval from graph (dynamic answers): {}", optOutcome);
      }
    } else if (questionType.equals(QuestionType.CONDITIONAL_NUMERIC_INPUT)) {
      // TODO: Implement!
      throw new AnswersValidationException("TODO - new query type!!");
    } else {
      throw new AnswersValidationException("Question / answer type not currently supported");
    }

    if (optOutcome.size() > 0) {
      return resolveOutcome(optOutcome);
    }

    // Graph is malformed or lookup service does not contain outcome
    throw new OutcomeException(currentQstnUuid, questionAnswers.getData());
  }

  private Outcome resolveOutcome(final List<QuestionInstanceOutcome> questionInstanceOutcomes) {
    log.debug("Resolving Outcome for QuestionInstanceOutcome: {}", questionInstanceOutcomes);
    if (questionInstanceOutcomes.size() == 1
        && questionInstanceOutcomes.get(0) instanceof QuestionInstance) {

      // TODO: Investigate why, when cast to a QuestionInstance the entity is not fully hydrated.
      // Refactor accordingly (should not be necessary to load same object from graph
      return new Outcome(OutcomeType.QUESTION,
          questionService.convertToQuestion(questionInstanceRepository
              .findById(((QuestionInstance) questionInstanceOutcomes.get(0)).getId(), 2).get()));
    } else if (allLots(questionInstanceOutcomes)) {
      return new Outcome(OutcomeType.LOT, LotList.fromItems(questionInstanceOutcomes));
    } else {
      throw new IllegalStateException(
          "Found either multiple QuestionInstance outcomes or mixture of Lot/other outcome"
              + questionInstanceOutcomes);
    }
  }

  private boolean allLots(final List<QuestionInstanceOutcome> questionInstanceOutcomes) {
    for (QuestionInstanceOutcome qic : questionInstanceOutcomes) {
      if (!(qic instanceof Lot)) {
        return false;
      }
    }
    return true;
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
        validateUuids(extractUuids(questionAnswers.getData()));
        break;
      case LIST:
        if (questionAnswers.getData().length != 1) {
          throw new AnswersValidationException("Question type 'list' expects single answer value");
        }
        validateUuids(extractUuids(questionAnswers.getData()));
        break;
      case MULTI_SELECT_LIST:
        if (questionAnswers.getData().length < 1) {
          throw new AnswersValidationException(
              "Question type 'multiSelectList' expects one or more answer values");
        }
        validateUuids(extractUuids(questionAnswers.getData()));
        break;
      case CONDITIONAL_NUMERIC_INPUT:
        // TODO
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

  private String[] extractUuids(final GivenAnswer[] givenAnswers) {
    return Arrays.stream(givenAnswers).map(GivenAnswer::getUuid).toArray(String[]::new);
  }

}
