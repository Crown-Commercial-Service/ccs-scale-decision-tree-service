/**
 *
 * QuestionsRepository.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import static uk.gov.crowncommercial.dts.scale.service.gm.model.UIComponentType.DROPDOWN;
import static uk.gov.crowncommercial.dts.scale.service.gm.model.UIComponentType.RADIO;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Answer;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;

/**
 * Questions repository
 *
 * TODO: Replace hardcoded data with dedicated persistent store
 */
@Repository
public class QuestionRepository {

  private static final Map<Integer, Question> questions = new HashMap<>();

  static final Set<Answer> quantityAnswers = new HashSet<>();
  static final Set<Answer> yesNoAnswers = new HashSet<>();
  static final Set<Answer> yesNoNotSureAnswers = new HashSet<>();
  static final Set<Answer> cameraTypesAnswers = new HashSet<>();


  static {
    quantityAnswers.add(new Answer(1, "Less than 100", 0));
    quantityAnswers.add(new Answer(2, "Between 100 and 1000", 1));
    quantityAnswers.add(new Answer(3, "Greater than 1000", 2));

    yesNoAnswers.add(new Answer(4, "Yes", 0));
    yesNoAnswers.add(new Answer(5, "No", 1));

    yesNoNotSureAnswers.addAll(yesNoAnswers);
    yesNoNotSureAnswers.add(new Answer(6, "Not sure", 2));

    cameraTypesAnswers.add(new Answer(7, "Compact Digital Camera", 0));
    cameraTypesAnswers.add(new Answer(8, "Digital SLR", 1));
    cameraTypesAnswers.add(new Answer(9, "Camcorder (video camera)", 2));
    cameraTypesAnswers.add(new Answer(10, "Dashcam", 3));
    cameraTypesAnswers.add(new Answer(11, "Thermal imaging camera", 4));
    cameraTypesAnswers.add(new Answer(12, "CCTV", 5));
    cameraTypesAnswers.add(new Answer(13, "Disposable camera", 6));
  }

  static {
    questions.put(1, new Question(1, "How many 'Laptops' do you require?", RADIO, quantityAnswers));
    questions.put(2, new Question(2, "Do you require any services to be included in your purchase?",
        RADIO, yesNoAnswers));
    questions.put(3, new Question(3, "Are you purchasing on behalf of an educational institution?",
        RADIO, yesNoAnswers));
    questions.put(4,
        new Question(4, "Is the purchase traffic management related?", RADIO, yesNoAnswers));
    questions.put(5, new Question(5, "Would you consider the purchase 'low volume'?", RADIO,
        yesNoNotSureAnswers));
    questions.put(6,
        new Question(6, "What type of camera do you require?", DROPDOWN, cameraTypesAnswers));
    questions.put(7, new Question(7, "How many 'Cameras' do you require?", RADIO, quantityAnswers));
  }

  public Question getQuestion(final int id) {
    return questions.get(id);
  }


}
