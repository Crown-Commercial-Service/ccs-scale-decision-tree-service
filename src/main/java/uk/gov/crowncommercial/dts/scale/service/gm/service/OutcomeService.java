package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.MULTI_SELECT_LIST;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.AnswersValidationException;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.GraphException;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.OutcomeException;
import uk.gov.crowncommercial.dts.scale.service.gm.model.*;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.*;
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

  /**
   * Attempt to find an 'outcome' (a {@link QuestionInstanceOutcome} instance which is either a
   * {@link QuestionInstance} or a collection of {@link Agreement}) from answer group based routing
   * defined in the graph. The result is then wrapped in a new {@link Outcome} object and returned,
   * or an exception thrown.
   *
   * @param currentQstnUuid
   * @param answerUuid
   * @return an Outcome instance containing either a question instance or an agreement
   * @throws OutcomeException if no outcome is discovered
   */
  public Outcome getQuestionInstanceOutcome(final String currentQstnUuid,
      final AnsweredQuestion[] answeredQuestions) {

    AnsweredQuestion firstAnsweredQuestion = answeredQuestions[0];

    log.debug("currentQstnUuid: {}, firstAnsweredQuestion: {}", currentQstnUuid,
        firstAnsweredQuestion);

    if (!firstAnsweredQuestion.getUuid().equals(currentQstnUuid)) {
      throw new IllegalStateException(
          "First answered question UUID does not match path param question UUID");
    }

    QuestionInstance questionInstance = questionInstanceRepository.findByUuid(currentQstnUuid)
        .orElseThrow(() -> new RuntimeException("TODO: QuestionInstance not found etc"));

    log.trace("currentQuestionInstance: {}", questionInstance);

    Set<GivenAnswer> givenAnswers = firstAnsweredQuestion.getAnswers();
    GivenAnswer firstGivenAnswer =
        givenAnswers.stream().findFirst().orElseThrow(() -> new RuntimeException(
            "TODO: 400 Bad Request (client must submit at least one answer)"));

    // Validate the incoming answers against the current question type:
    validateAnsweredQuestion(questionInstance, firstAnsweredQuestion);

    QuestionType questionType = questionInstance.getQuestion().getType();
    List<QuestionInstanceOutcome> outcomes;

    if (givenAnswers.size() == 1) {
      if (questionInstance.isConditionalInput() && isNotBlank(firstGivenAnswer.getValue())) {
        // TODO: Lookup the answer's conditional input type from the graph
        validateConditionalInput(QuestionType.NUMBER, firstGivenAnswer.getValue());

        outcomes = outcomeRepo.findSingleConditionalNumericAnswerOutcomes(currentQstnUuid,
            firstGivenAnswer.getUuid(), Double.parseDouble(firstGivenAnswer.getValue()));
        log.debug("Single conditional numeric input answer retrieval from graph: {}", outcomes);
      } else {
        // BOOLEAN, LIST, MULTI_SELECT_LIST
        outcomes =
            outcomeRepo.findSingleAnswerOutcomes(currentQstnUuid, firstGivenAnswer.getUuid());
        log.debug("Single answer outcome retrieval from graph via static answers: {}", outcomes);
      }
    } else if (questionType.equals(MULTI_SELECT_LIST)) {

      Set<MultiSelect> givenAnswersMultiSelects =
          filterMultiSelectByGivenAnwsers(questionInstance.getAnswerGroups(), givenAnswers);
      Optional<MultiSelect> chosenMultiSelect;

      Set<MultiSelect> primaryMultiSelects =
          givenAnswersMultiSelects.stream().filter(MultiSelect::isPrimary).collect(toSet());

      if (primaryMultiSelects.stream().map(MultiSelect::getGroup).collect(toSet()).size() == 1) {

        chosenMultiSelect = primaryMultiSelects.stream().findFirst();
        log.debug(
            "Multi select processing: Primary groups identical across given answer groups: {}",
            chosenMultiSelect);
      } else {
        chosenMultiSelect = givenAnswersMultiSelects.stream()
            .sorted(Comparator.comparing(MultiSelect::getMixPrecedence)).findFirst();
        log.debug(
            "Multi select processing: Primary groups mixed across given answer groups. Selected highest precedence: {}",
            chosenMultiSelect);
      }

      outcomes = outcomeRepo.findMultiAnswerOutcomes(currentQstnUuid, chosenMultiSelect
          .orElseThrow(() -> new IllegalStateException("Chosen MultiSelect not found")).getUuid());
      log.debug("Multi answer outcome retrieval from graph via static answers: {}", outcomes);

    } else {
      throw new AnswersValidationException("Question / answer type not currently supported");
    }

    if (!outcomes.isEmpty()) {
      return resolveOutcome(outcomes);
    }

    // Graph is malformed
    throw new OutcomeException(currentQstnUuid, givenAnswers);
  }

  /**
   * Calculate the relevant {@link MultiSelect}s by intersecting the set of given answer UUIDs with
   * those related to all answer groups for the current question instance
   *
   * @param answerGroups
   * @param givenAnswers
   * @return a collection of multi-select routings for the given answer set
   */
  private Set<MultiSelect> filterMultiSelectByGivenAnwsers(final Set<AnswerGroup> answerGroups,
      final Set<GivenAnswer> givenAnswers) {

    return answerGroups.stream().filter(ag -> {
      Set<String> agAnswerUuids = ag.getHasAnswerRels().stream().map(HasAnswer::getAnswer)
          .map(Answer::getUuid).collect(toSet());
      Set<String> givenAnswerUuids =
          givenAnswers.stream().map(GivenAnswer::getUuid).collect(toSet());

      return !Collections.disjoint(agAnswerUuids, givenAnswerUuids);
    }).flatMap(ag -> ag.getMultiSelects().stream()).collect(toSet());
  }

  private Outcome resolveOutcome(final List<QuestionInstanceOutcome> questionInstanceOutcomes) {
    log.debug("Resolving Outcome for QuestionInstanceOutcome: {}", questionInstanceOutcomes);
    if (questionInstanceOutcomes.size() == 1
        && questionInstanceOutcomes.get(0) instanceof QuestionInstance) {

      // TODO: Investigate why, when cast to a QuestionInstance the entity is not fully hydrated.
      // Refactor accordingly (should not be necessary to load same object from graph
      return new Outcome(OutcomeType.QUESTION, QuestionDefinitionList
          .fromItems(questionService.convertToQuestion(questionInstanceRepository
              .findById(((QuestionInstance) questionInstanceOutcomes.get(0)).getId(), 3).get())));
    } else if (allAgreements(questionInstanceOutcomes)) {
      return new Outcome(OutcomeType.AGREEMENT, AgreementList.fromItems(questionInstanceOutcomes));
    } else if (questionInstanceOutcomes.get(0) instanceof Support) {
      return new Outcome(OutcomeType.SUPPORT, null);
    } else {
      throw new GraphException(
          "Found neither a single QuestionInstance outcome nor multiple Agreements nor the Support type: "
              + questionInstanceOutcomes);
    }
  }

  private boolean allAgreements(final List<QuestionInstanceOutcome> questionInstanceOutcomes) {
    for (QuestionInstanceOutcome qic : questionInstanceOutcomes) {
      if (!(qic instanceof Agreement)) {
        return false;
      }
    }
    return true;
  }

  private void validateAnsweredQuestion(final QuestionInstance questionInstance,
      final AnsweredQuestion answeredQuestion) {

    log.debug("Validating answers: {} for question instance: {}", answeredQuestion.getAnswers(),
        questionInstance.getUuid());
    int numAnswers = answeredQuestion.getAnswers().size();

    switch (questionInstance.getQuestion().getType()) {

      case BOOLEAN:
        if (numAnswers != 1) {
          throw new AnswersValidationException(
              "Question type 'boolean' expects single answer value");
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
        break;
      case LIST:
        if (numAnswers != 1) {
          throw new AnswersValidationException("Question type 'list' expects single answer value");
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
        break;
      case MULTI_SELECT_LIST:
        if (numAnswers < 1) {
          throw new AnswersValidationException(
              "Question type 'multiSelectList' expects one or more answer values");
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
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

  private void validateUuids(final Set<String> answerValues) {
    for (String ans : answerValues) {
      try {
        UUID.fromString(ans);
      } catch (IllegalArgumentException ex) {
        throw new AnswersValidationException(
            format("Invalid UUID: %s, msg: %s", ans, ex.getMessage()));
      }
    }
  }

  private Set<String> extractUuids(final Set<GivenAnswer> givenAnswers) {
    return givenAnswers.stream().map(GivenAnswer::getUuid).collect(Collectors.toSet());
  }

  private void validateConditionalInput(final QuestionType questionType, final String answerValue) {
    switch (questionType) {
      case NUMBER:
        try {
          Double.parseDouble(answerValue);
        } catch (NumberFormatException nfe) {
          throw new AnswersValidationException(
              "Invalid NUMBER type conditional input value: " + answerValue);
        }
        break;
      default:
        throw new NotImplementedException(
            "Only NUMBER conditional input question types are implemented");
    }
  }

}
