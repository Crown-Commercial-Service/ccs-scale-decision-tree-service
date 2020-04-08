package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class QuestionDefinition {

  String uuid;
  String text;
  String hint;
  String pattern;
  QuestionType type;

}
