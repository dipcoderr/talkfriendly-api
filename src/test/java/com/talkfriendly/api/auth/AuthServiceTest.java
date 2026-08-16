package com.talkfriendly.api.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.talkfriendly.api.auth.dto.*;
import com.talkfriendly.api.common.exception.*;
import com.talkfriendly.api.security.JwtService;
import com.talkfriendly.api.user.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock PasswordEncoder passwords;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwt;
    @InjectMocks AuthService service;

    @Test void registersANewAccountWithEncodedPassword() {
        RegisterRequest request=new RegisterRequest("  SAM@Example.com ","a-long-secure-password"," Sam ");
        when(users.existsByEmail("sam@example.com")).thenReturn(false);
        when(passwords.encode(request.password())).thenReturn("hash");
        User user=new User("sam@example.com","hash","Sam");
        when(users.save(any(User.class))).thenReturn(user);
        when(jwt.generateAccessToken(any(),eq("sam@example.com"),eq("USER"))).thenReturn("token");

        AuthResponse result=service.register(request);

        assertThat(result.accessToken()).isEqualTo("token");
        assertThat(result.user().email()).isEqualTo("sam@example.com");
        assertThat(result.user().displayName()).isEqualTo("Sam");
        verify(passwords).encode(request.password());
    }

    @Test void rejectsDuplicateEmail() {
        when(users.existsByEmail("sam@example.com")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("sam@example.com","a-long-secure-password","Sam")))
            .isInstanceOf(ConflictException.class);
        verifyNoInteractions(passwords,jwt);
    }

    @Test void convertsBadCredentialsIntoSafeUnauthorizedResponse() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("details should not leak"));
        assertThatThrownBy(() -> service.login(new LoginRequest("sam@example.com","wrong-password")))
            .isInstanceOf(UnauthorizedException.class).hasMessage("Invalid email or password");
        verify(users,never()).findByEmail(anyString());
    }

    @Test void authenticatesAndReturnsToken() {
        User user=new User("sam@example.com","hash","Sam");
        when(users.findByEmail("sam@example.com")).thenReturn(Optional.of(user));
        when(jwt.generateAccessToken(any(),eq("sam@example.com"),eq("USER"))).thenReturn("token");
        AuthResponse response=service.login(new LoginRequest("SAM@example.com","password"));
        assertThat(response.accessToken()).isEqualTo("token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
