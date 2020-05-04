package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.AnswerDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionDefinition;
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
  private final LookupService lookupService;

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
              har.getAnswer().setOrder(har.getOrder());
              return har.getAnswer();
            }).collect(Collectors.toSet()).stream();
          }
          return lookupService.findAnswers(questionInstance.getUuid(), "TODO - modifier term")
              .stream();
        }).map(a -> AnswerDefinition.builder().uuid(a.getUuid()).text(a.getText()).hint(a.getHint())
            .order(a.getOrder()).build())
            .sorted(Comparator.comparingInt(AnswerDefinition::getOrder))
            .collect(Collectors.toList());

    return QuestionDefinition.builder().uuid(questionInstance.getUuid()).text(question.getText())
        .type(question.getType()).hint(question.getHint()).pattern(question.getPattern())
        .answerDefinitions(answerDefinitions).build();
  }

}
