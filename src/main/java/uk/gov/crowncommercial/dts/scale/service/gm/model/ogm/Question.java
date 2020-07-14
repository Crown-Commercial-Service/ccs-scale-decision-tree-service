package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uk.gov.crowncommercial.dts.scale.service.gm.model.QuestionType;
import uk.gov.crowncommercial.dts.scale.service.gm.model.Unit;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Question {

  String uuid;
  String text;
  String hint;
  String pattern;
  QuestionType type;
  Unit unit;

}
