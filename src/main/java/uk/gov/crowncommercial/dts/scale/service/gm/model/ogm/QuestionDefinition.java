/**
 *
 * QuestionDefinition.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.Data;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType;

/**
 *
 */
@Data
@NodeEntity
public class QuestionDefinition {

  String uuid;
  String text;
  String hint;
  String pattern;
  QuestionType type;

}
