package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Support implements QuestionInstanceOutcome {

  Long id;
  String uuid;

}
