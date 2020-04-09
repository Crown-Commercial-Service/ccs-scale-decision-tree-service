package uk.gov.crowncommercial.dts.scale.service.gm.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.DefinedAnswer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.HasAnswer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionDefinition;
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

    List<DefinedAnswer> definedAnswers = questionInstance.getAnswerGroups().stream().flatMap(ag -> {
      Optional<Set<HasAnswer>> hasAnswerRels = Optional.ofNullable(ag.getHasAnswerRels());

      if (hasAnswerRels.isPresent()) {
        return hasAnswerRels.get().stream().map(har -> {
          har.getAnswer().setOrder(har.getOrder());
          return har.getAnswer();
        }).collect(Collectors.toSet()).stream();
      }
      return lookupService.findAnswers(questionInstance.getUuid(), "TODO - modifier term").stream();
    }).map(a -> DefinedAnswer.builder().uuid(a.getUuid()).text(a.getText()).hint(a.getHint())
        .order(a.getOrder()).build()).sorted(Comparator.comparingInt(DefinedAnswer::getOrder))
        .collect(Collectors.toList());

    return Question.builder().uuid(questionInstance.getUuid()).text(qd.getText()).type(qd.getType())
        .hint(qd.getHint()).pattern(qd.getPattern()).definedAnswers(definedAnswers).build();
  }

}
