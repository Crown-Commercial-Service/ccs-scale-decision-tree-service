package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Set;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Url implements QuestionInstanceOutcome {

  String text;
  String hint;
  String link;
}
