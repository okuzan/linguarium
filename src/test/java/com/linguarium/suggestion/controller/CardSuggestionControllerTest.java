package com.linguarium.suggestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linguarium.auth.model.LocalUser;
import com.linguarium.configuration.security.PasswordEncoder;
import com.linguarium.configuration.security.jwt.TokenProvider;
import com.linguarium.configuration.security.oauth2.CustomOAuth2UserService;
import com.linguarium.configuration.security.oauth2.CustomOidcUserService;
import com.linguarium.configuration.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.linguarium.configuration.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.linguarium.suggestion.dto.CardSuggestionDto;
import com.linguarium.suggestion.service.CardSuggestionService;
import com.linguarium.user.model.User;
import com.linguarium.user.service.impl.LocalUserDetailServiceImpl;
import com.linguarium.util.TestDataGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardSuggestionController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class CardSuggestionControllerTest {
    static final String BASE_URL = "/api/cards/suggestions";
    static final String ACCEPT_CARD_URL = BASE_URL + "/{id}/accept";
    static final String DECLINE_CARD_URL = BASE_URL + "/{id}/decline";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CardSuggestionService cardSuggestionService;

    @MockBean
    LocalUserDetailServiceImpl localUserDetailsService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    CustomOidcUserService customOidcUserService;

    @MockBean
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @MockBean
    OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LocalUser localUser;

    @MockBean
    User user;

    @BeforeEach
    void setUp() {
        when(localUser.getUser()).thenReturn(user);
    }

    @DisplayName("Should suggest card")
    @Test
    void givenCardSuggestionDto_whenSuggestCard_thenCardSuggestionCreated() throws Exception {
        CardSuggestionDto dto = CardSuggestionDto.builder()
                .cardId(1L)
                .recipientId(2L)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(authentication(TestDataGenerator.getAuthenticationToken(localUser))))
                .andExpect(status().isCreated());
    }

    @DisplayName("Should accept a card suggestion for current user")
    @Test
    void givenCardId_whenAcceptCard_thenSuggestionAccepted() throws Exception {
        Long cardIdToAccept = 1L;

        mockMvc.perform(MockMvcRequestBuilders.put(ACCEPT_CARD_URL, cardIdToAccept)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(TestDataGenerator.getAuthenticationToken(localUser))))
                .andExpect(status().isNoContent());

        verify(cardSuggestionService).acceptSuggestion(cardIdToAccept, user);
    }

    @DisplayName("Should decline a card suggestion for current user")
    @Test
    void givenCardId_whenDeclineCard_thenSuggestionDeclined() throws Exception {
        Long cardIdToDecline = 1L;

        mockMvc.perform(MockMvcRequestBuilders.put(DECLINE_CARD_URL, cardIdToDecline)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(TestDataGenerator.getAuthenticationToken(localUser))))
                .andExpect(status().isNoContent());

        verify(cardSuggestionService).declineSuggestion(cardIdToDecline, user);
    }
}
