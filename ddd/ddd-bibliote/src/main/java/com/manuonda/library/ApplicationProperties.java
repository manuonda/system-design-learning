package com.manuonda.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app")

public record ApplicationProperties(
        @Value()
) {
}
