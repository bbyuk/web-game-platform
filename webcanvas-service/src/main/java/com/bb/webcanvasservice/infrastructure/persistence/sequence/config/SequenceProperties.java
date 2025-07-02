package com.bb.webcanvasservice.infrastructure.persistence.sequence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.sequence")
public record SequenceProperties(
    List<String> list
) {}
