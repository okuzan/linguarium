package com.linguarium.user.controller;

import com.linguarium.auth.annotation.CurrentUser;
import com.linguarium.auth.dto.UserInfo;
import com.linguarium.auth.model.LocalUser;
import com.linguarium.user.dto.LanguageUpdateRequest;
import com.linguarium.user.dto.UsernameAvailability;
import com.linguarium.user.dto.UsernameUpdateRequest;
import com.linguarium.user.service.LearnerService;
import com.linguarium.user.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    LearnerService learnerService;

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@CurrentUser LocalUser localUser) {
        return ResponseEntity.ok(userService.buildUserInfo(localUser.getUser()));
    }

    @GetMapping("/{username}/availability/")
    public ResponseEntity<UsernameAvailability> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(new UsernameAvailability(isAvailable));
    }

    @PutMapping("/me/username")
    public ResponseEntity<Void> updateUsername(
            @RequestBody UsernameUpdateRequest request, @CurrentUser LocalUser user) {
        userService.changeUsername(request.newUsername(), user.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/account")
    public ResponseEntity<Void> deleteCurrentUserAccount(@CurrentUser LocalUser user) {
        userService.deleteAccount(user.getUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/target-langs")
    public ResponseEntity<Void> updateTargetLanguages(
            @RequestBody LanguageUpdateRequest request, @CurrentUser LocalUser user) {
        learnerService.updateTargetLanguages(request.langCodes(), user.getUser().getLearner());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/fluent-langs")
    public ResponseEntity<Void> updateFluentLanguages(
            @RequestBody LanguageUpdateRequest request, @CurrentUser LocalUser user) {
        learnerService.updateFluentLanguages(request.langCodes(), user.getUser().getLearner());
        return ResponseEntity.noContent().build();
    }
}
