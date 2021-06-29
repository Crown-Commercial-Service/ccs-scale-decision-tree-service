package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType.MULTI_SELECT_LIST;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rollbar.notifier.Rollbar;

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

  @Autowired
  private Rollbar rollbar;
  
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
    	IllegalStateException e = new IllegalStateException("First answered question UUID does not match path param question UUID");
    	rollbar.error(e);
    	throw e;
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
    	AnswersValidationException e = new AnswersValidationException("Question / answer type not currently supported");
    	rollbar.error(e);
    	throw e;
    }

    if (!outcomes.isEmpty()) {
      return resolveOutcome(outcomes);
    }

    // Graph is malformed
    OutcomeException e = new OutcomeException(currentQstnUuid, givenAnswers);
    rollbar.error(e);
	throw e;
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
      return new Outcome(OutcomeType.QUESTION,
          QuestionDefinitionList
              .fromItems(questionService.convertToQuestion(questionInstanceRepository
                  .findById(((QuestionInstance) questionInstanceOutcomes.get(0)).getId(), 3)
                  .orElseThrow(() -> new GraphException("Could not find question")))));
    } else if (allAgreements(questionInstanceOutcomes)) {
      return new Outcome(OutcomeType.AGREEMENT, AgreementList.fromItems(questionInstanceOutcomes));
    } else if (questionInstanceOutcomes.get(0) instanceof Support) {
      return new Outcome(OutcomeType.SUPPORT, null);
    } else {
    	GraphException e = new GraphException("Found neither a single QuestionInstance outcome nor multiple Agreements nor the Support type: " + questionInstanceOutcomes);
    	rollbar.error(e);
    	throw e;
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
        	AnswersValidationException booleanave = new AnswersValidationException("Question type 'boolean' expects single answer value");
        	rollbar.error(booleanave);
        	throw booleanave;
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
        break;
      case LIST:
        if (numAnswers != 1) {
        	AnswersValidationException listave = new AnswersValidationException("Question type 'list' expects single answer value");
        	rollbar.error(listave);
        	throw listave;
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
        break;
      case MULTI_SELECT_LIST:
        if (numAnswers < 1) {
        	AnswersValidationException multiSelectListave = new AnswersValidationException("Question type 'multiSelectList' expects one or more answer values");
        	rollbar.error(multiSelectListave);
        	throw multiSelectListave;
        }
        validateUuids(extractUuids(answeredQuestion.getAnswers()));
        break;
      case TEXT_INPUT:
    	  NotImplementedException TEXT_INPUTe = new NotImplementedException("TEXT_INPUT question type not implemented");
    	  rollbar.error(TEXT_INPUTe);
    	  throw TEXT_INPUTe;
      case DATE:
    	  NotImplementedException DATEe = new NotImplementedException("DATE question type not implemented");
    	  rollbar.error(DATEe);
    	  throw DATEe;
      case DATE_RANGE:
    	  NotImplementedException DATE_RANGEe = new NotImplementedException("DATE_RANGE question type not implemented");
    	  rollbar.error(DATE_RANGEe);
    	  throw DATE_RANGEe;
      case NUMBER:
    	  NotImplementedException NUMBERe = new NotImplementedException("NUMBER question type not implemented");
    	  rollbar.error(NUMBERe);
    	  throw NUMBERe;
      case NUTS:
    	  NotImplementedException NUTSe = new NotImplementedException("NUTS question type not implemented");
    	  rollbar.error(NUTSe);
    	  throw NUTSe;
      case POSTCODE:
    	  NotImplementedException POSTCODEe = new NotImplementedException("POSTCODE question type not implemented");
    	  rollbar.error(POSTCODEe);
    	  throw POSTCODEe;
      default:
    	  NotImplementedException defaulte = new NotImplementedException("Unable to validate unsupported QuestionType");
    	  rollbar.error(defaulte);
    	  throw defaulte;
    }
  }

  private void validateUuids(final Set<String> answerValues) {
    for (String ans : answerValues) {
      try {
        UUID.fromString(ans);
      } catch (IllegalArgumentException ex) {
    	  AnswersValidationException e = new AnswersValidationException(format("Invalid UUID: %s", ans), ex);
    	  rollbar.error(e);
    	  throw e;
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
        	AnswersValidationException e = new AnswersValidationException("Invalid NUMBER type conditional input value: " + answerValue, nfe);
        	rollbar.error(e);
      	  	throw e;
        }
        break;
      default:
    	  NotImplementedException e = new NotImplementedException("Only NUMBER conditional input question types are implemented");
    	  rollbar.error(e);
    	  throw e;
    }
  }

}
