package com.manuonda.urlshortener.service;


import com.manuonda.urlshortener.domain.entities.ShortUrl;
import com.manuonda.urlshortener.domain.models.CreateShortUrlCmd;
import com.manuonda.urlshortener.domain.models.PagedResult;
import com.manuonda.urlshortener.domain.models.ShortUrlDto;
import com.manuonda.urlshortener.repositorys.ShortUrlRepository;
import com.manuonda.urlshortener.repositorys.UserRepository;
import com.manuonda.urlshortener.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.InvalidUrlException;

import javax.swing.text.html.Option;
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
        shortUrlRepository.save(shortUrl);

        // Initialize click count in cache
        if(shortUrl.getMaxClicks() > 0 ){
            this.urlCacheService.setClickLimit(shortKey, (long) shortUrl.getMaxClicks());
        }
        logger.info("Created short URL: {} -> {}", shortKey, cmd.originalUrl());

        return entityMapper.toShortUrlDto(shortUrl);
    }


    @Cacheable(value="SHOR_URL_CACHE", key="#shortKey")
    public ShortUrl getShortUrlFromCache(String shortKey){
        return shortUrlRepository.findByShortKey(shortKey).orElse(null);
    }
    @Transactional
    public Optional<ShortUrlDto> accessShortUrl(String shortKey, Long userId) {

        // Get from cache and db
        ShortUrl shortUrl = getShortUrlFromCache(shortKey);
        if (shortUrl == null) {
            return Optional.empty();
        }


        // validate expiration
        if(shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        // validate private
        if(shortUrl.getIsPrivate() != null && shortUrl.getIsPrivate()
                && shortUrl.getCreatedBy() != null
                && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)) {
            return Optional.empty();
        }

        if(this.urlCacheService.isClickLimitExceeded(shortUrl.getShortKey())){
            return Optional.empty();
        }

        urlCacheService.incrementClickCount(shortKey);

        if(shortUrl.getMaxClicks() > 0){
            this.urlCacheService.setClickLimit(shortKey, (long) shortUrl.getMaxClicks());
        }

        //shortUrl.setClickCount(shortUrl.getClickCount()+1);
        //shortUrlRepository.save(shortUrl);
        return Optional.of(entityMapper.toShortUrlDto(shortUrl));
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }

    public long countTotalClicks(String shortUrl){

        return 0;
    }
}
