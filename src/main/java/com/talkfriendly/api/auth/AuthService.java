package com.talkfriendly.api.auth;

import com.talkfriendly.api.auth.dto.*;
import com.talkfriendly.api.common.exception.*;
import com.talkfriendly.api.security.JwtService;
import com.talkfriendly.api.user.*;
import java.util.Locale;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder passwordEncoder; private final AuthenticationManager authenticationManager; private final JwtService jwtService;
    public AuthService(UserRepository users,PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,JwtService jwtService) { this.users=users;this.passwordEncoder=passwordEncoder;this.authenticationManager=authenticationManager;this.jwtService=jwtService; }
    @Transactional public AuthResponse register(RegisterRequest request) {
        String email=normalizeEmail(request.email());
        if (users.existsByEmail(email)) throw new ConflictException("An account already exists for this email address");
        User user=users.save(new User(email,passwordEncoder.encode(request.password()),request.displayName().trim()));
        return response(user);
    }
    public AuthResponse login(LoginRequest request) {
        String email=normalizeEmail(request.email());
        try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,request.password())); }
        catch (AuthenticationException ex) { throw new UnauthorizedException("Invalid email or password"); }
        User user=users.findByEmail(email).orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return response(user);
    }
    @Transactional(readOnly=true) public UserResponse currentUser(String email) { return UserResponse.from(users.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"))); }
    private AuthResponse response(User user) { return new AuthResponse(jwtService.generateAccessToken(user.getId(),user.getEmail(),user.getRole().name()),"Bearer",UserResponse.from(user)); }
    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
