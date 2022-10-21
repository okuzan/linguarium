package com.linguatool.controller;

import com.linguatool.configuration.CurrentUser;
import com.linguatool.model.dto.LocalUser;
import com.linguatool.model.dto.external_api.request.AnalysisDto;
import com.linguatool.model.dto.external_api.request.CardDto;
import com.linguatool.model.dto.external_api.response.words.WordsReportDto;
import com.linguatool.model.entity.user.Language;
import com.linguatool.service.ExternalService;
import com.linguatool.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @GetMapping("/stack-search/{text}")

    public ResponseEntity<List<CardDto>> search(@PathVariable String text, @CurrentUser LocalUser localUser) {
        return ResponseEntity.ok(userService.searchByEntry(text, localUser.getUser()));
    }

    @GetMapping("/translate/{langFrom}/{langTo}/{text}")
    public ResponseEntity<?> translate(@PathVariable String langFrom,
                                       @PathVariable String langTo,
                                       @PathVariable String text) {
        return ResponseEntity.ok(externalService.translate(text, Language.fromString(langFrom), Language.fromString(langTo)));
    }

    @PostMapping("/bulk-translate/{langTo}")
    public ResponseEntity<?> bulkTranslate(@PathVariable String langTo,
                                           @RequestBody String text) {
        return ResponseEntity.ok(externalService.bulkTranslate(text, Language.fromString(langTo)));
    }

    @GetMapping("/random")
    public ResponseEntity<WordsReportDto> random() {
        return ResponseEntity.ok(externalService.getRandom());
    }

    @GetMapping("/audio/{word}")
    public ResponseEntity<?> audio(@PathVariable String word) {
        return ResponseEntity.ok(externalService.getAudioLink(word));
    }

    @GetMapping("/audio/{lang}/{text}/file.mp3")
    public ResponseEntity<Resource> bulkPronounce(@PathVariable String lang, @PathVariable String text) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.mp3");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        byte[] bytes = externalService.textToSpeech(lang, text).toByteArray();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    @GetMapping("/file.mp3")
    public ResponseEntity<AnalysisDto> getReporddt() {
        return null;
    }

    @RequestMapping(path = "/download/file.mp3", method = RequestMethod.GET)
    public ResponseEntity<Resource> download() throws IOException {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.mp3");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        byte[] bytes = externalService.textToSpeech("en", "How come you didn't wake up early?").toByteArray();
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(bytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping("/report/{lang}/{text}")
    public ResponseEntity<AnalysisDto> getReport(@PathVariable String text,
                                                 @PathVariable String lang,
                                                 @CurrentUser LocalUser user) {
        return ResponseEntity.ok(externalService.getReport(text, lang, user.getUser()));
    }

}
