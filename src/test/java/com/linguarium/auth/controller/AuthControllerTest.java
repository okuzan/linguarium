package com.linguarium.auth.controller;

import com.linguarium.auth.dto.SocialProvider;
import com.linguarium.auth.dto.UserInfo;
import com.linguarium.auth.dto.request.LoginRequest;
import com.linguarium.auth.dto.request.RegistrationRequest;
import com.linguarium.auth.dto.response.JwtAuthenticationResponse;
import com.linguarium.auth.exception.UserAlreadyExistsAuthenticationException;
import com.linguarium.base.BaseControllerTest;
import com.linguarium.config.GlobalExceptionHandler;
import com.linguarium.user.service.UserService;
import com.linguarium.util.TestDataGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, includeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = GlobalExceptionHandler.class)
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest extends BaseControllerTest {
    private static final String BASE_URL = "/auth";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String REGISTER_URL = BASE_URL + "/register";

    @MockBean
    UserService userService;

    @DisplayName("Should authenticate user successfully")
    @Test
    void givenValidCredentials_whenLogin_thenReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        UserInfo userInfo = TestDataGenerator.buildTestUserInfo();

        JwtAuthenticationResponse response = new JwtAuthenticationResponse("xxx.yyy.zzz", userInfo);
        when(userService.login(eq(loginRequest))).thenReturn(response);

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Should handle BadCredentialsException by returning unauthorized status")
    void givenInvalidCredentials_whenLogin_thenReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongpassword");
        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @DisplayName("Should register user successfully")
    @Test
    void givenValidSignUpRequest_whenRegister_thenSuccess() throws Exception {
        RegistrationRequest registrationRequest = createSignUpRequest();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @DisplayName("Should handle existing user registration attempt by returning bad request status")
    @Test
    void givenExistingUser_whenRegister_thenBadRequest() throws Exception {
        RegistrationRequest registrationRequest = createSignUpRequest();

        doThrow(new UserAlreadyExistsAuthenticationException("User already exists"))
                .when(userService).register(any(RegistrationRequest.class));

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    private RegistrationRequest createSignUpRequest() {
        return RegistrationRequest.builder()
                .userId(1L)
                .providerUserId("dummyProviderUserId")
                .username("dummyUsername")
                .email("dummy@example.com")
                .socialProvider(SocialProvider.FACEBOOK)
                .profilePicLink("http://example.com/dummy.jpg")
                .password("dummyPassword123")
                .build();
    }
}