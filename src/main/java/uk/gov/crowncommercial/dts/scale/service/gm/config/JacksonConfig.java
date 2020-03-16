/**
 *
 * JacksonConfig.java
 *
 */
package uk.gov.crowncommercial.dts.scale.service.gm.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Configuration
@Slf4j
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer addCustomBigDecimalDeserialization() {
    log.info("Configuring Jackson behaviour...");
    return new Jackson2ObjectMapperBuilderCustomizer() {

      @Override
      public void customize(final Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
        jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
      }
    };
  }

}
