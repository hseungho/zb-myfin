package com.myfin.core;

import com.myfin.core.util.SeoulDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEntity {

    /** 엔티티 생성일시 */
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    /** 엔티티 최종수정일시 */
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    /** 엔티티 생성 시 호출. */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = SeoulDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** 엔티티 수정 시 호출. */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = SeoulDateTime.now();
    }

}
