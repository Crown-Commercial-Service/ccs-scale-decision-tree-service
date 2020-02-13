/**
 *
 * QuestionService.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.service;

import org.neo4j.ogm.session.Session;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.reposoitory.QuestionRepositoryNeo4J;

/**
 * GM Question Service component
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

  private final QuestionRepositoryNeo4J questionRepository;

  private final Session session;

  public Question getNextQuestion(final int questionId, final int answerId) {
    Long nextQuestionId = questionRepository.getNextQuestionId(questionId, answerId);
    return nextQuestionId != null ? session.load(Question.class, nextQuestionId) : null;
  }


  public Question getQuestion(final long id) {
    return questionRepository.findById(id, 1).orElse(null);
  }

}
