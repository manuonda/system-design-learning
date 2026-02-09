package com.manuonda.library.shared.infra;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


/**
 * Base Entity with Audit Metadata
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract  class BaseEntity {

     @Column(name="created_at", nullable = false, updatable = false)
     @CreatedDate
     protected Instant createdAt;

     @Column(name="updated_at" , nullable = false)
     @LastModifiedDate
     protected Instant updatedAt;

}
