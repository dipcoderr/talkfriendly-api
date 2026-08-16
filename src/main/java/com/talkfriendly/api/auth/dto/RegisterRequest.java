package com.talkfriendly.api.auth.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank @Email @Size(max=320) String email, @NotBlank @Size(min=12, max=72) String password, @NotBlank @Size(min=1,max=80) String displayName) { }
