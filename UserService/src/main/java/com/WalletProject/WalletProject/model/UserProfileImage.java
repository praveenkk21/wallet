package com.WalletProject.WalletProject.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Lob
    @Column(name = "image")
    private Blob image;  // Store the image as a BLOB

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void onPrePersist() {
        // Set the createdAt field before the entity is saved
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
