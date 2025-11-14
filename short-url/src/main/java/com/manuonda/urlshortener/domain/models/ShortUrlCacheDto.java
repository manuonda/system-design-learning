package com.manuonda.urlshortener.domain.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.time.Instant;

/**
 * DTO mínimo para cachear en Redis
 * Solo datos esenciales, sin relaciones complejas (createdBy Entity)
 * Serializable sin problemas de Jackson
 *
 * @JsonTypeInfo asegura que Jackson siempre incluya el tipo como propiedad @class en la serialización
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public record ShortUrlCacheDto(
        Long id,
        String shortKey,
        String originalUrl,
        Boolean isPrivate,
        Instant expiresAt,
        Long createdById,       // ID del usuario creador (para validar privacidad)
        Long clickCount,
        Integer maxClicks
) implements Serializable {
}