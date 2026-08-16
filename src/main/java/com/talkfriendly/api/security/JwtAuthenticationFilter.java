package com.talkfriendly.api.security;

import com.talkfriendly.api.user.User;
import com.talkfriendly.api.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService; private final UserRepository users;
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository users) { this.jwtService=jwtService; this.users=users; }
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        String header=request.getHeader("Authorization");
        if (header==null || !header.startsWith("Bearer ")) { chain.doFilter(request,response); return; }
        String token=header.substring(7);
        if (jwtService.isValid(token) && SecurityContextHolder.getContext().getAuthentication()==null) {
            try {
                User user=users.findById(jwtService.extractUserId(token)).orElse(null);
                if (user!=null) {
                    var auth=new UsernamePasswordAuthenticationToken(user.getEmail(),null,java.util.List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (RuntimeException ignored) { SecurityContextHolder.clearContext(); }
        }
        chain.doFilter(request,response);
    }
}
