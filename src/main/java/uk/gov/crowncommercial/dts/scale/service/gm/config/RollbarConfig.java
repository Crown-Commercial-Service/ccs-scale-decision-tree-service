package uk.gov.crowncommercial.dts.scale.service.gm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;

@Configuration
public class RollbarConfig {

  @Value("${rollbar.access.token:1111111111}") 
  private String rollbarAccessToken;

  @Value("${environment:test}")
  private String environment;

  /**
   * Register a Rollbar bean to configure App with Rollbar.
   */
  @Bean
  public Rollbar rollbar() {
    return new Rollbar(getRollbarConfigs(rollbarAccessToken));
  }

  private Config getRollbarConfigs(String accessToken) {

    // Reference ConfigBuilder.java for all the properties you can set for Rollbar
    return RollbarSpringConfigBuilder.withAccessToken(rollbarAccessToken)
            .environment(environment)
            .build();
  }
}
