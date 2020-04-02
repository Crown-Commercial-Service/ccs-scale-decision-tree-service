package uk.gov.crowncommercial.dts.scale.service.gm.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson configuration
 */
@Configuration
@Slf4j
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer addCustomBigDecimalDeserialization() {
    log.debug("Configuring Jackson behaviour...");

    return jacksonObjectMapperBuilder -> {
      jacksonObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
      jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    };
  }

}
