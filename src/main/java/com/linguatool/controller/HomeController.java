package com.linguatool.controller;

import com.linguatool.annotation.CurrentUser;
import com.linguatool.model.dto.LocalUser;
import com.linguatool.model.dto.external_api.request.CardDto;
import com.linguatool.service.CardService;
import com.linguatool.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final CardService cardService;

    public HomeController(CardService userService) {
        this.cardService = userService;
    }

    @GetMapping("/cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardDto>> getCurrentUser(@CurrentUser LocalUser user) {
        return ResponseEntity.ok(cardService.getUsersCards(user.getUser()));
    }
}
