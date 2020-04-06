package uk.gov.crowncommercial.dts.scale.service.gm.model.ogm;

import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.lang.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;

/**
 *
 */
@Data
@NodeEntity
public class Answer {

  String uuid;
  String text;
  
  @Nullable
  String hint;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Nullable
  String outcomeUuid;

}
