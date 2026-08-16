package com.talkfriendly.api.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 320) private String email;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Column(name = "display_name", nullable = false, length = 80) private String displayName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private Role role;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected User() { }
    public User(String email, String passwordHash, String displayName) { this.id=UUID.randomUUID(); this.email=email; this.passwordHash=passwordHash; this.displayName=displayName; this.role=Role.USER; }
    @PrePersist void onCreate() { createdAt=Instant.now(); updatedAt=createdAt; }
    @PreUpdate void onUpdate() { updatedAt=Instant.now(); }
    public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getDisplayName(){return displayName;} public Role getRole(){return role;} public Instant getCreatedAt(){return createdAt;}
}
