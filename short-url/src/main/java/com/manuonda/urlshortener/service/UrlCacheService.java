package com.manuonda.urlshortener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manuonda.urlshortener.domain.models.ShortUrlCacheDto;
import com.manuonda.urlshortener.repositorys.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UrlCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UrlCacheService.class);
    private static final String CLICKS_PREFIX = "clicks:";
    private static final String LIMIT_PREFIX = "limit:";
    private static final String SHORT_URL_PREFIX = "shorturl:";


    private final RedisTemplate<String, Object> redisTemplate;
    private final ShortUrlRepository shortUrlRepository;

    public UrlCacheService(RedisTemplate<String, Object> redisTemplate, ShortUrlRepository shortUrlRepository) {
        this.redisTemplate = redisTemplate;
        this.shortUrlRepository = shortUrlRepository;
    }


    /**
     * INCREMENTS the click count automatically using cache
     * Important: This method does not update the database directly.
     * @param shortKey
     * @return the number of clicks after incrementing
     */
    public long incrementAndGetClickCount(String shortKey) {
        try {
            Long newClickCount = redisTemplate.opsForValue().increment(CLICKS_PREFIX + shortKey);
            logger.debug("Clicked count for shortKey {}  - {}", shortKey, newClickCount);
            return newClickCount != null ? newClickCount : 0L;
        }catch (Exception e){
            logger.error("Error while increment count for shortKey {}", shortKey, e);
            return 0L;
        }
    }

    /**
     * Gets the click count from cache
     * @param shortKey
     * @return
     */
    public long getClickCount(String shortKey){
        try{
            Object value = redisTemplate.opsForValue().get(CLICKS_PREFIX + shortKey);
            if(Objects.nonNull(value)){
                return Long.parseLong(value.toString());
            }
        }catch (Exception e){
            logger.error("Error while getting click count for shortKey {}", shortKey, e);
            return 0L;
        }
        return 0L;
    }

    /**
     * Sets the click limit for a short URL
     * @param shortKey
     * @param maxClicks
     */
    public void setClickLimit(String shortKey, Long maxClicks){
        try{
            if(maxClicks != null && maxClicks > 0){
                redisTemplate.opsForValue().set(LIMIT_PREFIX + shortKey, maxClicks.toString());
                logger.debug("Set click limit for shortKey {}  - {}", shortKey, maxClicks);
            } else {
                redisTemplate.delete(LIMIT_PREFIX + shortKey);
                logger.debug("Removed click limit for shortKey {}", shortKey);
            }
        }catch (Exception e){
            logger.error("Error while setting click limit for shortKey {}", shortKey, e);
        }
    }

    /**
     *  Get the limit of clicks for a short URL.
     */
    public long getClickLimit(String shortKey) {
        try {
            Object limitStr = redisTemplate.opsForValue().get(LIMIT_PREFIX + shortKey);
            return (limitStr != null) ? Long.parseLong(limitStr.toString()) : 0L;
        } catch (Exception e) {
            logger.error("Error obteniendo límite para {}", shortKey, e);
            return 0L;
        }
    }

    /**
     * Verifica si se ha excedido el límite de clics.
     *
     * @param shortKey La clave corta
     * @param currentClickCount El contador actual (después de incrementar)
     * @param maxClicks El límite máximo
     * @return true si currentClickCount > maxClicks
     */
    public boolean isClickLimitExceeded(String shortKey, long currentClickCount, long maxClicks) {
        if (maxClicks <= 0) {
            return false; // Sin límite
        }
        boolean exceeded = currentClickCount > maxClicks;
        if (exceeded) {
            logger.warn("Límite de clics excedido para {}: {} > {}", shortKey, currentClickCount, maxClicks);
        }
        return exceeded;
    }




    /**
     * Scheduled task to synchronize click counts from Redis to the database every 5 minutes.
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void synchronizeClicksToDatabase() {
        try {
            logger.info("Iniciando sincronización de clics a BD...");

            Set<String> keys = redisTemplate.keys(CLICKS_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                logger.info("No hay clics para sincronizar.");
                return;
            }

            int syncCount = 0;
            for (String key : keys) {
                String shortKey = key.substring(CLICKS_PREFIX.length());
                Object clicksStr = redisTemplate.opsForValue().get(key);

                if (clicksStr != null) {
                    long clicksInRedis =  Long.parseLong(clicksStr.toString());

                    shortUrlRepository.findByShortKey(shortKey).ifPresent(shortUrl -> {
                        if (clicksInRedis > shortUrl.getClickCount()) {
                            shortUrl.setClickCount(clicksInRedis);
                            shortUrlRepository.save(shortUrl);
                            logger.info("Sincronizado: {} -> {} clics", shortKey, clicksInRedis);
                        }
                    });
                    syncCount++;
                }
            }

            logger.info("Sincronización completada. {} URLs procesadas.", syncCount);
        } catch (Exception e) {
            logger.error("Error durante sincronización de clics", e);
        }
    }

    /**
     * Increments the click count for a given short URL.
     * @param shortKey
     */
    public void incrementClickCount(String shortKey) {
        long newClickCount = incrementAndGetClickCount(shortKey);
        long maxClicks = getClickLimit(shortKey);
        if (isClickLimitExceeded(shortKey, newClickCount, maxClicks)) {
            logger.warn("Click limit exceeded for shortKey {}: {}/{}", shortKey, newClickCount, maxClicks);
        }
      }

    /**
     * Get ShortUrl DTO from cache
     * @param shortKey
     * @return ShortUrlCacheDto if found in cache, null otherwise
     */
    public ShortUrlCacheDto getShortUrlFromCache(String shortKey){
        try{
            Object object = this.redisTemplate.opsForValue().get(SHORT_URL_PREFIX + shortKey);
            if(Objects.nonNull(object)){
                logger.debug("Object found in cache, type: {}", object.getClass().getSimpleName());
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(object, ShortUrlCacheDto.class);
            } else {
                logger.info("Cache MISS for shortKey: {} (object is null)", shortKey);
                return null;
            }
        }catch (Exception e){
            logger.error("Exception getting ShortUrl from cache for shortKey {}", shortKey, e);
            return null;
        }
    }

    /**
     * Invalidate the cache for a short URL
     * @param shortKey
     */
    public void invalidateShortUrlCache(String shortKey){
        try{
            logger.info("Invalidating ShortUrl cache for shortKey {}", shortKey);
            redisTemplate.delete(SHORT_URL_PREFIX + shortKey);
        }catch (Exception e){
            logger.error("Error invalidating ShortUrl cache for shortKey {}", shortKey, e);
        }
    }

    /**
     * Cache ShortUrl DTO (minimal DTO without problematic fields)
     * @param shortKey
     * @param cacheDto ShortUrlCacheDto (not the full ShortUrlDto)
     */
    public void cacheShortUrl(String shortKey, ShortUrlCacheDto cacheDto) {
        try{
            logger.info("Attempting to cache ShortUrlCacheDto for shortKey: {}", shortKey);
            if(cacheDto == null) {
                logger.warn("cacheDto is null, will not cache");
                return;
            }
            redisTemplate.opsForValue().set(SHORT_URL_PREFIX + shortKey, cacheDto, 1, TimeUnit.HOURS);
            logger.info("Successfully cached ShortUrlCacheDto for shortKey: {} (TTL: 1 hour)", shortKey);
        }catch (Exception e){
            logger.error("Exception caching ShortUrlCacheDto for shortKey: {}", shortKey, e);
            logger.error("Exception details: ", e);
        }
    }
}