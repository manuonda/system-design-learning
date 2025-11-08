package com.manuonda.urlshortener.repositorys;

import com.manuonda.urlshortener.domain.entities.ShortUrl;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.JpaRepositoryNameSpaceHandler;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
   List<ShortUrl> findByIsPrivateIsFalseOrderByCreatedAtDesc();

//   @Query(
//           """
//           Select su from ShortUrl su
//           left join fetch  su.createdBy
//           where su.isPrivate = false
//           order by su.createdAt desc
//           """
//   )
   @Query("SELECT su from ShortUrl  su where su.isPrivate = false")
   @EntityGraph(attributePaths ={"createdBy"})
   Page<ShortUrl> findPublicShortUrl(Pageable pageable);


   boolean existsByShortKey(String shortKey);

   Optional<ShortUrl>  findByShortKey(String shortKey);

    Page<ShortUrl> findByCreatedById(Long userId, Pageable pageable);

   @Modifying
   void deleteByIdInAndCreatedById(List<Long> ids, Long userId);

   @Query("select u from ShortUrl u left join fetch u.createdBy")
   Page<ShortUrl> findAllShortUrls(Pageable pageable);


   @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false")
   Page<ShortUrl> findPublicShortUrls(Pageable pageable);

}
