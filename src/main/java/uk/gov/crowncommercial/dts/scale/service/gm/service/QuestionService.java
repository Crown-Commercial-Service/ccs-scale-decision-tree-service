package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.exception.GraphException;
import uk.gov.crowncommercial.dts.scale.service.gm.model.AnswerDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ConditionalInput;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionDefinitionList;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Answer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.HasAnswer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;
import uk.gov.crowncommercial.dts.scale.service.gm.repository.QuestionInstanceRepositoryNeo4J;

/**
 * GM QuestionInstance Service component
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

  private final QuestionInstanceRepositoryNeo4J questionRepository;

  public QuestionDefinitionList getQuestionDefinitionList(final String uuid) {
    return QuestionDefinitionList.fromItems(getQuestion(uuid));
  }

  public QuestionDefinition getQuestion(final String uuid) {
    Optional<QuestionInstance> questionInstance = questionRepository.findByUuid(uuid);

    if (questionInstance.isPresent()) {
      return convertToQuestion(questionInstance.get());
    }
    return null;
  }

  public QuestionDefinition convertToQuestion(final QuestionInstance questionInstance) {
    log.debug("Converting QuestionInstance: {}", questionInstance);
    Question question = questionInstance.getQuestion();

    List<AnswerDefinition> answerDefinitions =
        questionInstance.getAnswerGroups().stream().flatMap(ag -> {
          Optional<Set<HasAnswer>> hasAnswerRels = Optional.ofNullable(ag.getHasAnswerRels());

          if (hasAnswerRels.isPresent()) {
            return hasAnswerRels.get().stream().map(har -> {

              // Set transient field values to augment Answer definition
              har.getAnswer().setOrder(har.getOrder());
              har.getAnswer().setMutex(har.isMutex());
              return har.getAnswer();
            }).collect(Collectors.toSet()).stream();
          }
          throw new GraphException("TODO: No answer relations found");
        })
            // Map each Answer to an AnswerDefinition
            .map(a -> AnswerDefinition.builder().uuid(a.getUuid()).text(a.getText())
                .hint(a.getHint()).order(a.getOrder())
                .conditionalInput(getConditionalInputFromAnswer(a).orElseGet(() -> null))
                .mutuallyExclusive(a.isMutex()).build())
            .sorted(Comparator.comparingInt(AnswerDefinition::getOrder))
            .collect(Collectors.toList());

    return QuestionDefinition.builder().uuid(questionInstance.getUuid()).text(question.getText())
        .type(question.getType()).hint(question.getHint()).pattern(question.getPattern())
        .answerDefinitions(answerDefinitions).build();
  }

  private Optional<ConditionalInput> getConditionalInputFromAnswer(final Answer answer) {
    return Optional.ofNullable(answer.getConditionalInputQuestion())
        .map(q -> new ConditionalInput(q.getText(), q.getHint(), q.getType()));
  }
}
