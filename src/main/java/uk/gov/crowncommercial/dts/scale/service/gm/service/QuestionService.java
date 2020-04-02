package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.DefinedAnswer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionDefinition;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;
import uk.gov.crowncommercial.dts.scale.service.gm.reposoitory.QuestionInstanceRepositoryNeo4J;

/**
 * GM QuestionInstance Service component
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

  private final QuestionInstanceRepositoryNeo4J questionRepository;
  private final LookupService lookupService;

  public Question getQuestion(final String uuid) {
    Optional<QuestionInstance> questionInstance = questionRepository.findByUuid(uuid);

    if (questionInstance.isPresent()) {
      return convertToQuestion(questionInstance.get());
    }
    return null;
  }

  public Question convertToQuestion(final QuestionInstance questionInstance) {
    log.debug("Converting QuestionInstance: {}", questionInstance);
    QuestionDefinition qd = questionInstance.getQuestionDefinition();

    Set<DefinedAnswer> definedAnswers = questionInstance.getAnswerGroups().stream()
        .flatMap(ag -> Optional.ofNullable(ag.getAnswers())
            .orElseGet(() -> lookupService.findAnswers(questionInstance.getUuid(), "housing"))
            .stream())
        .map(a -> new DefinedAnswer(a.getUuid(), a.getText())).collect(Collectors.toSet());

    return Question.builder().uuid(questionInstance.getUuid()).text(qd.getText()).type(qd.getType())
        .hint(qd.getHint()).pattern(qd.getPattern()).definedAnswers(definedAnswers).build();
  }

}
