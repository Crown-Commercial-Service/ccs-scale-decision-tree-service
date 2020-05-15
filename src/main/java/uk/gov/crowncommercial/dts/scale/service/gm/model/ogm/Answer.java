package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity
public class Answer {

  String uuid;
  String text;
  String hint;
  String outcomeUuid;

  @Transient
  Integer order;

  @Transient
  boolean mutex;

  // Prototype..
  String conditionalInputText;
  String conditionalInputHint;
}
