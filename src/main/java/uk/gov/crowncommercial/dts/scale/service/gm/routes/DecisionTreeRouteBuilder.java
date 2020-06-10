package uk.gov.crowncommercial.dts.scale.service.gm.routes;

import static java.lang.Boolean.TRUE;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import uk.gov.crowncommercial.dts.scale.service.gm.model.AnsweredQuestion;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyStart;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Outcome;
import uk.gov.crowncommercial.dts.scale.service.gm.service.JourneyService;
import uk.gov.crowncommercial.dts.scale.service.gm.service.OutcomeService;

/**
 * Camel routing configuration (WIP)
 *
 */
@Component
@RequiredArgsConstructor
public class DecisionTreeRouteBuilder extends EndpointRouteBuilder {

  private static final String JSON_BINDING = RestBindingMode.json.name();
  private static final String PATH_JOURNEYS = "/journeys";
  private static final String ROUTE_DIRECT_FINALISE_RESPONSE = "direct:finalise-response";

  private final JourneyService journeyService;
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
     * Get Journey
     */
    rest()
      .get(PATH_JOURNEYS + "/{journey-uuid}")
      .outType(JourneyStart.class)
      .param().name("journey-uuid").type(RestParamType.path).required(TRUE).endParam()
      .to("direct:get-journey");

    from("direct:get-journey")
      .log(LoggingLevel.INFO, "Get Journey invoked")
      .bean(journeyService, "getJourney(${headers[journey-uuid]})")
      .to(ROUTE_DIRECT_FINALISE_RESPONSE);

    /*
     * Get journey questionInstance outcome
     */
    rest()
      .post(PATH_JOURNEYS + "/{uuid}/questions/{question-uuid}/outcome")
      .type(AnsweredQuestion[].class)
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
      .to(ROUTE_DIRECT_FINALISE_RESPONSE);

    from(ROUTE_DIRECT_FINALISE_RESPONSE)
      .removeHeaders("*")
      .setHeader("Access-Control-Allow-Origin", constant("*"));

    // @formatter:on
  }

}
