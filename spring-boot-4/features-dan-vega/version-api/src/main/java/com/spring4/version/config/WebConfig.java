package com.spring4.version.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API Versioning configuration.
 *
 * Uncomment ONE strategy at a time — both cannot be active simultaneously.
 *
 * STRATEGY A — Path Segment:
 *   Active controller : UserController       (/api/{version}/users)
 *   Example           : http :8080/api/v1/users
 *
 * STRATEGY B — Request Header:
 *   Active controller : UserHeaderController (/header/users)
 *   Example           : http :8080/header/users X-API-Version:1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {

        // STRATEGY A — path segment: /api/v1/users  /api/v2/users
        configurer
                .addSupportedVersions("1.0", "1.1", "2.0")
                .setDefaultVersion("1.0")
                .setVersionParser(new ApiVersionParser())
                .usePathSegment(1);

        // STRATEGY B — request header: X-API-Version: 1.0
        // configurer
        //         .addSupportedVersions("1.0", "1.1", "2.0")
        //         .setDefaultVersion("1.0")
        //         .setVersionParser(new ApiVersionParser())
        //         .useRequestHeader("X-API-Version");
    }
}