package com.linguarium.analyzer.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.protobuf.ByteString;
import com.linguarium.analyzer.dto.AnalysisDto;
import com.linguarium.analyzer.service.impl.LanguageProcessor;
import com.linguarium.auth.model.LocalUser;
import com.linguarium.base.BaseControllerTest;
import com.linguarium.card.dto.CardDto;
import com.linguarium.card.service.CardService;
import com.linguarium.client.words.dto.WordsReportDto;
import com.linguarium.translator.dto.MLTranslationCard;
import com.linguarium.translator.dto.TranslationCardDto;
import com.linguarium.translator.model.Language;
import com.linguarium.user.model.Learner;
import com.linguarium.util.TestDataGenerator;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

@WebMvcTest(LangController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AutoConfigureMockMvc(addFilters = false)
class LangControllerTest extends BaseControllerTest {
    private static final String BASE_URL = "/lang/";
    private static final String TRANSLATE_URL = BASE_URL + "translate/{langFrom}/{langTo}/{text}";
    private static final String REPORT_URL = BASE_URL + "words/{text}/{lang}/report";
    private static final String BULK_PRONOUNCE_URL = BASE_URL + "words/{text}/audio/{lang}";
    private static final String RANDOM_URL = BASE_URL + "words/random";
    private static final String BULK_TRANSLATE_URL = BASE_URL + "translations/{langTo}/bulk";
    private static final String SEARCH_URL = BASE_URL + "cards/search/{text}";

    @MockBean
    CardService cardService;

    @MockBean
    LanguageProcessor languageProcessor;

    LocalUser principal;
    Learner learner;

    @BeforeEach
    void setUp() {
        principal = TestDataGenerator.createLocalUser();
        learner = principal.getUser().getLearner();
        SecurityContextHolder.getContext().setAuthentication(TestDataGenerator.getAuthenticationToken(principal));
    }

    @DisplayName("Should find cards by search text when called with valid text")
    @Test
    void givenSearchTextAndUser_whenSearchCards_thenReturnMatchingCards() throws Exception {
        String searchText = "hello";
        CardDto[] dummyCards = TestDataGenerator.createCardDtos();
        when(cardService.searchByEntry(searchText, learner)).thenReturn(List.of(dummyCards));

        mockMvc.perform(get(SEARCH_URL, searchText).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dummyCards)));

        verify(cardService).searchByEntry(searchText, learner);
    }

    @DisplayName("Should translate text from one language to another")
    @Test
    void givenTextAndLanguagePair_whenTranslateText_thenReturnTranslation() throws Exception {
        String langFrom = Language.EN.name();
        String langTo = Language.ES.name();
        String text = "Hello";
        TranslationCardDto translationCardDto = TestDataGenerator.createTranslationCardDto();

        when(languageProcessor.translate(text, Language.EN, Language.ES)).thenReturn(translationCardDto);

        mockMvc.perform(get(TRANSLATE_URL, langFrom, langTo, text).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(translationCardDto)));
    }

    @DisplayName("Should get a report for a given text and language")
    @Test
    void givenTextAndLanguage_whenGetReport_thenReturnAnalysis() throws Exception {
        String text = "Hello";
        String lang = Language.EN.name();
        AnalysisDto analysisDto = TestDataGenerator.createTestAnalysisDto();
        when(languageProcessor.getReport(text, lang, principal.getUser().getLearner()))
                .thenReturn(analysisDto);

        mockMvc.perform(get(REPORT_URL, text, lang).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(analysisDto)));
    }

    @DisplayName("Should bulk translate text to a specified language")
    @Test
    void givenTextAndTargetLanguage_whenBulkTranslate_thenReturnTranslations() throws Exception {
        String langTo = Language.ES.name();
        String text = "Hello";
        MLTranslationCard mlTranslationCard = TestDataGenerator.createMLTranslationCard();

        when(languageProcessor.bulkTranslate(text, Language.ES)).thenReturn(mlTranslationCard);

        mockMvc.perform(post(BULK_TRANSLATE_URL, langTo)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(text))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mlTranslationCard)));
    }

    @DisplayName("Should bulk pronounce text in a specified language")
    @Test
    void givenTextAndLanguage_whenBulkPronounce_thenReturnAudio() throws Exception {
        String lang = Language.EN.name();
        String text = "Hello";
        ByteString audioBytes = TestDataGenerator.generateRandomAudioBytes();
        when(languageProcessor.textToSpeech(lang, text)).thenReturn(audioBytes);

        mockMvc.perform(get(BULK_PRONOUNCE_URL, text, lang).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=file.mp3"))
                .andExpect(content().bytes(audioBytes.toByteArray()));
    }

    @DisplayName("Should get a random WordsReportDto")
    @Test
    void whenGetRandomWordsReport_thenReturnRandomWordsReportDto() throws Exception {
        // Arrange
        WordsReportDto wordsReportDto = TestDataGenerator.createEmptyWordsReportDto();
        when(languageProcessor.getRandom()).thenReturn(wordsReportDto);

        // Act & Assert
        mockMvc.perform(get(RANDOM_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(wordsReportDto)));
    }
}
