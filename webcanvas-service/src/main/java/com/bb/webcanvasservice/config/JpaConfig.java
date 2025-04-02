package com.bb.webcanvasservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.bb.webcanvasservice"})
public class JpaConfig {
}
