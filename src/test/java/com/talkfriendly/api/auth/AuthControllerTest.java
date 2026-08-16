package com.talkfriendly.api.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.talkfriendly.api.auth.dto.*;
import com.talkfriendly.api.common.exception.GlobalExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;

class AuthControllerTest {
    private AuthService service; private MockMvc mvc;
    @BeforeEach void setUp() { service=mock(AuthService.class); mvc=MockMvcBuilders.standaloneSetup(new AuthController(service)).setControllerAdvice(new GlobalExceptionHandler()).build(); }
    @Test void returnsCreatedTokenOnRegistration() throws Exception {
        when(service.register(any())).thenReturn(new AuthResponse("token","Bearer",new UserResponse(UUID.randomUUID(),"sam@example.com","Sam",Instant.now())));
        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"sam@example.com\",\"password\":\"a-long-secure-password\",\"displayName\":\"Sam\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.accessToken").value("token")).andExpect(jsonPath("$.user.email").value("sam@example.com"));
    }
    @Test void returnsFieldErrorsForInvalidRequest() throws Exception {
        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
