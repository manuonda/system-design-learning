package com.manuonda.urlshortener.service;


import com.manuonda.urlshortener.domain.entities.ShortUrl;
import com.manuonda.urlshortener.domain.models.ShortUrlDto;
import com.manuonda.urlshortener.domain.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

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
                shortUrl.getCreatedAt()
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
