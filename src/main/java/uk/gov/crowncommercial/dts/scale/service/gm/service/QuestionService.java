/**
 *
 * QuestionService.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static org.apache.commons.lang3.tuple.ImmutableTriple.of;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.reposoitory.QuestionRepository;

/**
 * GM Question Service component
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

  /**
   * <code>Map&lt;Triple&lt;JourneyId, QuestionId, GivenAnswerId&gt;, NextQuestionId&gt;</code>
   */
  private static final Map<Triple<Integer, Integer, Integer>, Integer> nextQuestions =
      new HashMap<>();

  static {

    /**
     * TODO: Refactor / replace. This is a (*very*) temporary data structure to allow us to cheat
     * somewhat and access the next question in a journey directly, without any persistent state (or
     * having to submit cumulative questnio-answer pairs at each stage.
     */

    // Scenarios 1 & 2 (laptops):
    nextQuestions.put(of(1, 1, 1), 2);
    nextQuestions.put(of(1, 1, 2), 2);
    nextQuestions.put(of(1, 1, 3), 2);
    nextQuestions.put(of(1, 2, 4), 3);
    nextQuestions.put(of(1, 2, 5), 3);

    // Scenarios 3 & 4 (cameras):
    nextQuestions.put(of(2, 4, 4), 5);
    nextQuestions.put(of(2, 4, 5), 6);

    nextQuestions.put(of(2, 6, 7), 7);
    nextQuestions.put(of(2, 6, 8), 7);
    nextQuestions.put(of(2, 6, 9), 7);
    nextQuestions.put(of(2, 6, 10), 7);
    nextQuestions.put(of(2, 6, 11), 7);
    nextQuestions.put(of(2, 6, 12), 7);
    nextQuestions.put(of(2, 6, 13), 7);

    nextQuestions.put(of(2, 7, 1), 2);
    nextQuestions.put(of(2, 7, 2), 2);
    nextQuestions.put(of(2, 7, 3), 2);

    nextQuestions.put(of(2, 2, 4), 3);
    nextQuestions.put(of(2, 2, 5), 3);
  }

  private final QuestionRepository questionRepository;

  public Question getNextQuestion(final int journeyId, final int questionId, final int answerId) {
    Integer nextQuestionId = nextQuestions.get(ImmutableTriple.of(journeyId, questionId, answerId));

    return nextQuestionId != null ? getQuestion(nextQuestionId) : null;
  }


  public Question getQuestion(final int id) {
    return questionRepository.getQuestion(id);
  }

}
