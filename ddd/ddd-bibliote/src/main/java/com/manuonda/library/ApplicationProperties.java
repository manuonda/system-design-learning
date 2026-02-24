package com.manuonda.library;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        @DefaultValue("dgarcia") String issuer,
        public record OpenApiProperties(
                @DefaultValue("Library API") String title,
                @DefaultValue("1.0.0") String version
        ){}
) {
}
