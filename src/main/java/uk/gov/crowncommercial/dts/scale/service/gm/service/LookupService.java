/**
 *
 * LookupService.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.service;

import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Answer;

/**
 *
 */
@Service
@Slf4j
public class LookupService {

  private final String findAnswersUriTemplate;
  private final String getAnswerUriTemplate;
  private final RestTemplate restTemplate;

  public LookupService(final RestTemplateBuilder restTemplateBuilder,
      @Value("${external.lookup-service.url}") final String lkpServiceBaseURL,
      @Value("${external.lookup-service.uri-templates.find-answers}") final String findAnswersUriTemplate,
      @Value("${external.lookup-service.uri-templates.get-answer}") final String getAnswerUriTemplate) {

    this.restTemplate = restTemplateBuilder.rootUri(lkpServiceBaseURL).build();
    this.findAnswersUriTemplate = findAnswersUriTemplate;
    this.getAnswerUriTemplate = getAnswerUriTemplate;
  }

  public Set<Answer> findAnswers(final String questionInstanceUuid, final String modifier) {

    Map<String, String> uriTemplateVars = new HashMap<>();
    uriTemplateVars.put("question-instance-uuid", questionInstanceUuid);
    uriTemplateVars.put("modifier", modifier);

    log.debug("LookupService findAnswers: {} with params: {}", findAnswersUriTemplate,
        uriTemplateVars);

    return new HashSet<>(asList(restTemplate
        .getForEntity(findAnswersUriTemplate, Answer[].class, uriTemplateVars).getBody()));
  }

  public Answer getAnswer(final String answerUuid) {
    return restTemplate.getForObject(getAnswerUriTemplate, Answer.class, answerUuid);
  }

}
