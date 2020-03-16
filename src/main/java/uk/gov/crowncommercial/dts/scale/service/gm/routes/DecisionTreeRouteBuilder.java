package uk.gov.crowncommercial.dts.scale.service.gm.routes;

import static java.lang.Boolean.TRUE;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Outcome;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionAnswers;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.model.ogm.QuestionInstance;
import uk.gov.crowncommercial.dts.scale.service.gm.service.JourneyService;
import uk.gov.crowncommercial.dts.scale.service.gm.service.OutcomeService;
import uk.gov.crowncommercial.dts.scale.service.gm.service.QuestionService;

/**
 * Camel routing configuration (WIP)
 *
 */
@Component
@RequiredArgsConstructor
public class DecisionTreeRouteBuilder extends EndpointRouteBuilder {

  private static final String JSON_BINDING = RestBindingMode.json.name();
  private static final String PATH_JOURNEYS = "/journeys";

  private final JourneyService journeyService;
  private final QuestionService questionService;
  private final OutcomeService outcomeService;

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
     * Get journey questionInstance
     */
    rest()
      .get(PATH_JOURNEYS + "/{uuid}/questions/{question-uuid}")
      .outType(QuestionInstance.class)
      .param().name("uuid").type(RestParamType.path).required(TRUE).endParam()
      .param().name("question-uuid").type(RestParamType.path).required(TRUE).endParam()
      .to("direct:get-journey-questionInstance");

    from("direct:get-journey-questionInstance")
      .log(LoggingLevel.INFO, "Journey get questionInstance invoked")
      .bean(questionService, "getQuestion(${headers[question-uuid]})")
      .to("direct:finalise-response");

    /*
     * Get journey questionInstance outcome
     */
    rest()
      .post(PATH_JOURNEYS + "/{uuid}/questions/{question-uuid}/outcome")
      .type(QuestionAnswers.class)
      .outType(Outcome.class)
      .param().name("uuid").type(RestParamType.path).required(TRUE).endParam()
      .param().name("question-uuid").type(RestParamType.path).required(TRUE).endParam()
      .to("direct:get-journey-next-questionInstance");

    from("direct:get-journey-next-questionInstance")
      .log(LoggingLevel.INFO, "Journey get questionInstance outcome invoked")
      .bean(outcomeService, "getQuestionInstanceOutcome(${headers[question-uuid]}, ${body})")
      .choice()
        .when(simple("${body} == null"))
          .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
          .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
          .setBody(constant("{\"errors\":[]}"))
        .end()
      .to("direct:finalise-response");

    from("direct:finalise-response")
      .setHeader("Access-Control-Allow-Origin", constant("*"));

    // @formatter:on
  }

}
