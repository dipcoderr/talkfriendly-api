package com.talkfriendly.api.user;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, UUID> { Optional<User> findByEmail(String email); boolean existsByEmail(String email); }
