package com.manuonda.urlshortener.service;


import com.manuonda.urlshortener.domain.entities.ShortUrl;
import com.manuonda.urlshortener.domain.models.ShortUrlCacheDto;
import com.manuonda.urlshortener.domain.models.ShortUrlDto;
import com.manuonda.urlshortener.domain.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    /**
     * Convierte Entity a DTO completo con datos del usuario
     * Usado para respuestas HTTP
     */
    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        UserDto userDto = null;
        if(shortUrl.getCreatedBy() != null){
            userDto = toUserDto(shortUrl.getCreatedBy());
        }
        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt(),
                shortUrl.getMaxClicks() != null ? shortUrl.getMaxClicks() : 0
        );
    }

    /**
     * Convierte Entity a DTO mínimo para Redis
     * Solo datos esenciales, sin relaciones complejas
     * Serializable por Jackson sin problemas
     */
    public ShortUrlCacheDto toShortUrlCacheDto(ShortUrl shortUrl) {
        Long createdById = shortUrl.getCreatedBy() != null ? shortUrl.getCreatedBy().getId() : null;
        return new ShortUrlCacheDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                createdById,
                shortUrl.getClickCount(),
                shortUrl.getMaxClicks()
        );
    }

    public UserDto toUserDto(com.manuonda.urlshortener.domain.entities.User user) {
        if(user == null) return null;
        return new UserDto(
                user.getId(),
                user.getName()
        );
    }
}
