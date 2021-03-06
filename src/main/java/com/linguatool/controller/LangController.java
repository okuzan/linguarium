package com.linguatool.controller;

import com.linguatool.configuration.CurrentUser;
import com.linguatool.model.dto.LocalUser;
import com.linguatool.model.dto.api.request.CardCreationDto;
import com.linguatool.model.dto.api.request.CardDto;
import com.linguatool.model.dto.api.response.words.WordsReportDto;
import com.linguatool.service.ExternalService;
import com.linguatool.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/lang")
public class LangController {

    final UserServiceImpl userService;

    @Autowired
    ExternalService externalService;

    public LangController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @CrossOrigin
    @GetMapping("/search/{text}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDto>> search(@PathVariable String text, @CurrentUser LocalUser localUser) {
        System.out.println(localUser.getUser().getCards());
        return ResponseEntity.ok(userService.searchByEntry(text, localUser.getUser()));
    }

    @GetMapping("/translate/{langFrom}/{langTo}/{text}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> translate(@PathVariable String langFrom,
                                       @PathVariable String langTo,
                                       @PathVariable String text) {
        return externalService.translate(langFrom, langTo, text);
    }

    @GetMapping("/random")
    public ResponseEntity<WordsReportDto> random() {
        return ResponseEntity.ok(externalService.getRandom());
    }

    @GetMapping("/audio/{word}")
    public ResponseEntity<?> audio(@PathVariable String word) {
        return ResponseEntity.ok(externalService.getAudioLink(word));
    }

    @GetMapping("/report/{text}/")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDto>> getReport(@PathVariable String text, @CurrentUser LocalUser localUser) {
//        externalService.getReport();
        return ResponseEntity.ok(userService.searchByEntry(text, localUser.getUser()));
    }

}
