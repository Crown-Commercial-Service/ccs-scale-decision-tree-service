/**
 *
 * JourneyRepository.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.reposoitory;

import static java.util.Optional.of;
import static org.apache.commons.collections4.MapUtils.putAll;
import java.util.*;
import org.springframework.stereotype.Repository;
import lombok.extern.slf4j.Slf4j;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Journey;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResponses;
import uk.gov.crowncommercial.dts.scale.service.gm.model.JourneyResult;

/**
 * Journeys repository
 *
 * TODO: Replace hardcoded data with dedicated persistent store / DT solution
 */
@Repository
@Slf4j
public class JourneyRepository {

  /**
   * Map of journey IDs to DT start node (i.e. the first question).
   */
  private static final Map<Integer, JourneyNode> DECISION_TREE = new HashMap<>();

  private static final JourneyResult JR_TFMT2 = new JourneyResult("Traffic Management Technology 2",
      "Transport technology, including traffic signals and CCTV, parking and access control, street lighting, intelligent transport systems and professional services.",
      "RM1089", "");
  private static final JourneyResult JR_TP2_LAPTOPS = new JourneyResult("Technology Products 2",
      "Technology Products 2 (TP2) offers public sector customers a flexible and compliant way to source all their technology product needs (hardware and software)",
      "RM3733", "http://35.176.126.74:3000/t/computing/mobile-devices/laptops");

  private static final JourneyResult JR_TP2_CAMERAS = new JourneyResult("Technology Products 2",
      "Technology Products 2 (TP2) offers public sector customers a flexible and compliant way to source all their technology product needs (hardware and software)",
      "RM3733", "http://35.176.126.74:3000/t/computing/mobile-devices/cameras");

  private static final JourneyResult JR_TPAS =
      new JourneyResult("Technology Products & Associated Services",
          "Offers public sector buyers a compliant route to market for technology product needs (hardware and software) and all associated services.",
          "RM6068", "");


  private static final Map<String, Set<Journey>> searchTermJourneys = new HashMap<>();

  static {
    Journey[] journeys = new Journey[] {
        new Journey(1, "Laptops Guided Match",
            new String[] {"laptop", "laptops", "ultrabook", "notebook", "notebooks"}, 1),
        new Journey(2, "Cameras Guided Match",
            new String[] {"camera", "cameras", "slr", "camcorder"}, 4)};

    // One search term may relate to multiple GM journeys e.g. "computer" could be desktops or
    // laptops
    for (Journey j : journeys) {
      for (String searchTerm : j.getSearchTerms()) {
        searchTermJourneys.computeIfAbsent(searchTerm, s -> new HashSet<>()).add(j);
      }
    }
  }

  static {

    JourneyNode journeyNodeTPAS = JourneyNode.builder().result(of(JR_TPAS)).build();
    JourneyNode journeyNodeTP2Laptops = JourneyNode.builder().result(of(JR_TP2_LAPTOPS)).build();
    JourneyNode journeyNodeTFMT2 = JourneyNode.builder().result(of(JR_TFMT2)).build();

    JourneyNode journeyNodeJ1Q3 = JourneyNode.builder().questionId(of(3)).answerProgressionNodes(of(
        putAll(new HashMap<>(), new Object[][] {{4, journeyNodeTPAS}, {5, journeyNodeTP2Laptops}})))
        .build();

    JourneyNode journeyNodeJ1Q2 = JourneyNode.builder().questionId(of(2))
        .answerProgressionNodes(of(
            putAll(new HashMap<>(), new Object[][] {{4, journeyNodeJ1Q3}, {5, journeyNodeJ1Q3}})))
        .build();

    JourneyNode journeyNodeJ1Q1 = JourneyNode.builder().questionId(of(1))
        .answerProgressionNodes(of(putAll(new HashMap<>(),
            new Object[][] {{1, journeyNodeJ1Q2}, {2, journeyNodeJ1Q2}, {3, journeyNodeJ1Q2}})))
        .build();

    DECISION_TREE.put(1, journeyNodeJ1Q1);

    JourneyNode journeyNodeJ2Q3 =
        JourneyNode.builder().questionId(of(3)).answerProgressionNodes(of(putAll(new HashMap<>(),
            new Object[][] {{4, journeyNodeTP2Laptops}, {5, journeyNodeTP2Laptops}}))).build();

    JourneyNode journeyNodeJ2Q2 = JourneyNode.builder().questionId(of(2))
        .answerProgressionNodes(of(
            putAll(new HashMap<>(), new Object[][] {{4, journeyNodeJ2Q3}, {5, journeyNodeJ2Q3}})))
        .build();

    JourneyNode journeyNodeJ2Q7 = JourneyNode.builder().questionId(of(7))
        .answerProgressionNodes(of(putAll(new HashMap<>(),
            new Object[][] {{1, journeyNodeJ2Q2}, {2, journeyNodeJ2Q2}, {3, journeyNodeJ2Q2}})))
        .build();

    JourneyNode journeyNodeJ2Q6 = JourneyNode.builder().questionId(of(6))
        .answerProgressionNodes(of(putAll(new HashMap<>(),
            new Object[][] {{7, journeyNodeJ2Q7}, {8, journeyNodeJ2Q7}, {9, journeyNodeJ2Q7},
                {10, journeyNodeJ2Q7}, {11, journeyNodeJ2Q7}, {12, journeyNodeJ2Q7},
                {13, journeyNodeJ2Q7}})))
        .build();

    JourneyNode journeyNodeJ2Q5 = JourneyNode.builder().questionId(of(5))
        .answerProgressionNodes(of(putAll(new HashMap<>(),
            new Object[][] {{4, journeyNodeTFMT2}, {5, journeyNodeTFMT2}, {6, journeyNodeTFMT2}})))
        .build();

    JourneyNode journeyNodeJ2Q4 = JourneyNode.builder().questionId(of(4))
        .answerProgressionNodes(of(
            putAll(new HashMap<>(), new Object[][] {{4, journeyNodeJ2Q5}, {5, journeyNodeJ2Q6}})))
        .build();

    DECISION_TREE.put(2, journeyNodeJ2Q4);
  }

  /**
   * Recursively walks the "decision tree" to obtain a Journey result, based on the response set
   * from the user
   *
   * @param journeyId
   * @param journeyResponses
   * @return a JourneyResult
   */
  public JourneyResult getJourneyResult(final int journeyId,
      final JourneyResponses journeyResponses) {

    JourneyNode currentNode = DECISION_TREE.get(journeyId);

    log.trace("Start node: {}", currentNode);

    JourneyNode nodeEnd = null;
    int i = 0;

    log.info("Starting to traverse decision tree for journeyId: [{}] with responses: [{}]",
        journeyId, journeyResponses);

    do {
      log.trace("Traversal iteration: [{}], currentNode: [{}]", i, currentNode);
      if (i == 20) {
        throw new IllegalStateException("Max recursions reached without result");
      }
      if (currentNode.getResult() != null && currentNode.getResult().isPresent()) {
        nodeEnd = currentNode;
      } else {
        Optional<Map<Integer, JourneyNode>> answerProgressionNodes =
            currentNode.getAnswerProgressionNodes();

        if (answerProgressionNodes == null || !answerProgressionNodes.isPresent()) {
          throw new IllegalStateException(
              "Answer progression nodes null or empty for current node: " + currentNode);
        }
        currentNode = answerProgressionNodes.get()
            .get(journeyResponses.getResponses().get(i++).getAnswerId());
      }

    } while (nodeEnd == null);

    log.info("Decision tree traversal complete in {} steps", i + 1);

    return nodeEnd.getResult().get();
  }

  public Set<Journey> searchJourneys(final String searchTerm) {
    return searchTermJourneys.getOrDefault(searchTerm, Collections.emptySet());
  }

}
