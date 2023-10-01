package com.linguarium.suggestion.service;

import com.linguarium.card.dto.CardDto;
import com.linguarium.suggestion.dto.CardAcceptanceDto;
import com.linguarium.suggestion.dto.CardSuggestionDto;
import com.linguarium.user.model.User;

import java.util.List;

public interface CardSuggestionService {
    List<CardDto> getSuggestedCards(User user);

    void declineSuggestion(CardAcceptanceDto dto, User recipient);

    void acceptSuggestion(CardAcceptanceDto dto, User recipient);

    boolean suggestCard(CardSuggestionDto dto, User sender);
}