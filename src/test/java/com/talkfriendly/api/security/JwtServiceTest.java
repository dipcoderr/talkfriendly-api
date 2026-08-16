package com.talkfriendly.api.security;

import static org.assertj.core.api.Assertions.*;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private final JwtService service=new JwtService(new JwtProperties("c2FmZS10ZXN0LXNlY3JldC1tdXN0LWJlLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=",Duration.ofMinutes(15)));
    @Test void createsAndValidatesAToken() {
        UUID id=UUID.randomUUID(); String token=service.generateAccessToken(id,"sam@example.com","USER");
        assertThat(service.isValid(token)).isTrue();
        assertThat(service.extractUserId(token)).isEqualTo(id);
    }
    @Test void treatsMalformedTokenAsInvalid() { assertThat(service.isValid("not-a-token")).isFalse(); }
}
