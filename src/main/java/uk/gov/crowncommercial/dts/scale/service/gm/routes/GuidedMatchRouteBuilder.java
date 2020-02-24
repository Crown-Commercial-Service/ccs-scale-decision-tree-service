package uk.gov.crowncommercial.dts.scale.service.gm.routes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResponses;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResult;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Question;
import uk.gov.crowncommercial.dts.scale.service.gm.service.JourneyService;
import uk.gov.crowncommercial.dts.scale.service.gm.service.QuestionService;

/**
 * Camel routing configuration (WIP)
 *
 */
@Component
@RequiredArgsConstructor
public class GuidedMatchRouteBuilder extends EndpointRouteBuilder {

  private static final String JSON_BINDING = RestBindingMode.json.name();
  private static final String PATH_JOURNEYS = "/journeys";

  private final JourneyService journeyService;
  private final QuestionService questionService;

  /*
   * (non-Javadoc)
   *
   * @see org.apache.camel.builder.RouteBuilder#configure()
   */
  @Override
  public void configure() throws Exception {
    // @formatter:off
    restConfiguration()
      .component("servlet")
      .bindingMode(JSON_BINDING);

    /*
     * Search journeys
     */
    rest()
      .get(PATH_JOURNEYS)
      .outType(Journey[].class)
      .param().name("q").type(RestParamType.query).required(TRUE).endParam()
      .to("direct:search-journeys");

    from("direct:search-journeys")
      .log(LoggingLevel.INFO, "Journey search invoked")
      .bean(journeyService, "searchJourneys(${headers[q]})")
      .to("direct:finalise-response");

    /*
     * Get journey question
     */
    rest()
      .get(PATH_JOURNEYS + "/{id}/questions/{question-id}")
      .outType(Question.class)
      .param().name("id").type(RestParamType.path).required(TRUE).endParam()
      .param().name("question-id").type(RestParamType.path).required(TRUE).endParam()
      .to("direct:get-journey-question");

    from("direct:get-journey-question")
      .log(LoggingLevel.INFO, "Journey get question invoked")
      .bean(questionService, "getQuestion(${headers[question-id]})")
      .to("direct:finalise-response");

    /*
     * Get journey next question
     */
    rest()
      .get(PATH_JOURNEYS + "/{id}/questions/{question-id}/next")
      .outType(Question.class)
      .param().name("id").type(RestParamType.path).required(TRUE).endParam()
      .param().name("question-id").type(RestParamType.path).required(TRUE).endParam()
      .param().name("answer-id").type(RestParamType.query).required(FALSE).endParam()
      .to("direct:get-journey-next-question");

    from("direct:get-journey-next-question")
      .log(LoggingLevel.INFO, "Journey get next question invoked")
      .bean(questionService, "getNextQuestion(${headers[question-id]}, ${headers[answer-id]})")
      .choice()
        .when(simple("${body} == null"))
          .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
          .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
          .setBody(constant("{\"errors\":[]}"))
        .end()
      .to("direct:finalise-response");

    /*
     * Submit (POST) Guided Match results
     */
    rest()
      .post(PATH_JOURNEYS + "/{id}/results")
      .type(JourneyResponses.class)
      .outType(JourneyResult.class)
      .to("direct:post-journey-results");

    from("direct:post-journey-results")
      .log(LoggingLevel.INFO, "Journey results invoked with: ${body}")
      .bean(journeyService, "getJourneyResult(${headers[id]}, ${body})")
      .to("direct:finalise-response");

    from("direct:finalise-response")
      .setHeader("Access-Control-Allow-Origin", constant("*"));

    // @formatter:on
  }

}
