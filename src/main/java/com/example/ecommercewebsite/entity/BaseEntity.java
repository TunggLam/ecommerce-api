package com.example.ecommercewebsite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Data
@MappedSuperclass
public class BaseEntity {
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;
}
