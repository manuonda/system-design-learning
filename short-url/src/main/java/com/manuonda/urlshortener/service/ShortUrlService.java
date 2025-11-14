package com.manuonda.urlshortener.service;


import com.manuonda.urlshortener.domain.entities.ShortUrl;
import com.manuonda.urlshortener.domain.models.CreateShortUrlCmd;
import com.manuonda.urlshortener.domain.models.PagedResult;
import com.manuonda.urlshortener.domain.models.ShortUrlCacheDto;
import com.manuonda.urlshortener.domain.models.ShortUrlDto;
import com.manuonda.urlshortener.domain.models.UserDto;
import com.manuonda.urlshortener.repositorys.ShortUrlRepository;
import com.manuonda.urlshortener.repositorys.UserRepository;
import com.manuonda.urlshortener.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.manuonda.urlshortener.service.RandomUtils.generateRandomShortKey;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {


    private static final Logger logger = LoggerFactory.getLogger(ShortUrlService.class);

    private final ShortUrlRepository shortUrlRepository;
    private final EntityMapper entityMapper;
    private final ApplicationProperties properties;
    private final UserRepository userRepository;
    private final UrlCacheService urlCacheService;

    public ShortUrlService(ShortUrlRepository shortUrlRepository,
                           EntityMapper entityMapper,
                           ApplicationProperties properties,
                           UserRepository userRepository, UrlCacheService urlCacheService) {
        this.shortUrlRepository = shortUrlRepository;
        this.entityMapper = entityMapper;
        this.properties = properties;
        this.userRepository = userRepository;
        this.urlCacheService = urlCacheService;
    }


    public PagedResult<ShortUrlDto> findAllPublicShortUrls(int pageNo, int pageSize) {
        Pageable pageable = getPageable(pageNo, pageSize);
        Page<ShortUrlDto> shortUrlDtoPage = shortUrlRepository.findPublicShortUrls(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }

    public PagedResult<ShortUrlDto> getUserShortUrls(Long userId, int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage = shortUrlRepository.findByCreatedById(userId, pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepository.deleteByIdInAndCreatedById(ids, userId);
        }
    }

    public PagedResult<ShortUrlDto> findAllShortUrls(int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage =  shortUrlRepository.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }

    private Pageable getPageable(int page, int size) {
        page = page > 1 ? page - 1: 0;
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if(properties.validateOriginalUrl()) {
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExists) {
                throw new RuntimeException("Invalid URL "+cmd.originalUrl());
            }
        }
        var shortKey = generateUniqueShortKey();
        var shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);
        if(cmd.userId() == null) {
            shortUrl.setCreatedBy(null);
            shortUrl.setIsPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
        } else {
            shortUrl.setCreatedBy(userRepository.findById(cmd.userId()).orElseThrow());
            shortUrl.setIsPrivate(cmd.isPrivate() != null && cmd.isPrivate());
            shortUrl.setExpiresAt(cmd.expirationInDays() != null ? Instant.now().plus(cmd.expirationInDays(), DAYS) : null);
        }
        shortUrl.setClickCount(0L);
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setMaxClicks(cmd.maxClicks());
        shortUrlRepository.save(shortUrl);

        // Initialize click count in cache
        if(shortUrl.getMaxClicks() > 0 ){
            this.urlCacheService.setClickLimit(shortKey, (long) shortUrl.getMaxClicks());
        }
        logger.info("Created short URL: {} -> {}", shortKey, cmd.originalUrl());

        return entityMapper.toShortUrlDto(shortUrl);
    }


    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {

        // Try to get from cache first
        ShortUrlCacheDto cacheDto = this.urlCacheService.getShortUrlFromCache(shortKey);
        ShortUrlDto shortUrlDto;

        if (cacheDto != null) {
            // Cache hit - convert cache DTO to full DTO
            logger.info("Using cached ShortUrl for shortKey: {}", shortKey);
            shortUrlDto = convertCacheDtoToDto(cacheDto);
        } else {
            // Cache miss - fetch from DB
            Optional<ShortUrl> shortUrlOpt = shortUrlRepository.findByShortKey(shortKey);
            if (shortUrlOpt.isEmpty()) {
                return Optional.empty();
            }
            ShortUrl shortUrl = shortUrlOpt.get();
            shortUrlDto = entityMapper.toShortUrlDto(shortUrl);

            // Store minimal DTO in cache for next time
            ShortUrlCacheDto cacheDtoToStore = entityMapper.toShortUrlCacheDto(shortUrl);
            this.urlCacheService.cacheShortUrl(shortKey, cacheDtoToStore);
        }

        // Validate expiration
        if(shortUrlDto.expiresAt() != null && shortUrlDto.expiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        // Validate private
        if(shortUrlDto.isPrivate() != null && shortUrlDto.isPrivate()
                && shortUrlDto.createdBy() != null
                && !Objects.equals(shortUrlDto.createdBy().id(), userId)) {
            return Optional.empty();
        }

        // Validate click limit
        long currentClicks = urlCacheService.getClickCount(shortKey);
        long maxClicks = shortUrlDto.maxClicks() != null ? shortUrlDto.maxClicks() : 0;

        // Validate click limit BEFORE incrementing
        if (maxClicks > 0 && currentClicks >= maxClicks) {
            this.urlCacheService.invalidateShortUrlCache(shortUrlDto.shortKey());
            return Optional.empty();
        }

        // Increment click count
        urlCacheService.incrementAndGetClickCount(shortKey);

        return Optional.of(shortUrlDto);
    }

    /**
     * Helper method to convert ShortUrlCacheDto to ShortUrlDto
     * Note: createdBy will be null (only ID is cached)
     * createdAt is not cached (not needed for access logic)
     */
    private ShortUrlDto convertCacheDtoToDto(ShortUrlCacheDto cacheDto) {
        // Reconstruct a minimal UserDto if createdById exists
        UserDto userDto = null;
        if (cacheDto.createdById() != null) {
            // Create a minimal UserDto with only the ID (name is not cached)
            userDto = new UserDto(cacheDto.createdById(), null);
        }

        return new ShortUrlDto(
                cacheDto.id(),
                cacheDto.shortKey(),
                cacheDto.originalUrl(),
                cacheDto.isPrivate(),
                cacheDto.expiresAt(),
                userDto,  // UserDto with ID only
                cacheDto.clickCount(),
                null,     // createdAt not cached (not used in validation)
                cacheDto.maxClicks()
        );
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

}
