package linguarium.auth.local.service;

import static linguarium.user.core.service.impl.UserUtility.getUser;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import linguarium.auth.common.factory.PrincipalFactory;
import linguarium.auth.common.service.AuthManagementService;
import linguarium.auth.local.dto.request.LocalAuthRequest;
import linguarium.auth.local.dto.response.JwtAuthResponse;
import linguarium.auth.local.exception.EmailNotFoundException;
import linguarium.auth.local.exception.EmailNotVerifiedException;
import linguarium.auth.local.exception.InvalidTokenException;
import linguarium.auth.local.exception.UserAlreadyExistsException;
import linguarium.auth.local.model.entity.LocalPrincipal;
import linguarium.auth.local.model.entity.VerificationToken;
import linguarium.auth.local.repository.LocalPrincipalRepository;
import linguarium.auth.local.repository.VerificationTokenRepository;
import linguarium.auth.local.service.impl.LocalAuthServiceImpl;
import linguarium.config.security.jwt.TokenProvider;
import linguarium.user.core.model.entity.Profile;
import linguarium.user.core.model.entity.User;
import linguarium.user.core.repository.UserRepository;
import linguarium.user.core.service.ProfileService;
import linguarium.user.core.service.UserService;
import linguarium.util.TestDataGenerator;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = PRIVATE)
class LocalAuthServiceImplTest {
    @InjectMocks
    LocalAuthServiceImpl authService;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    LocalPrincipalRepository localPrincipalRepository;

    @Mock
    PrincipalFactory principalFactory;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    AuthManagementService authManagementService;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    ProfileService profileService;

    @Mock
    VerificationTokenRepository verificationTokenRepository;

    @DisplayName("Should successfully register local user")
    @Test
    void givenValidLocalRequest_whenRegister_thenSaveUser() {
        // Arrange
        LocalAuthRequest registrationRequest = TestDataGenerator.createLocalAuthRequest();
        User user = User.builder().email(registrationRequest.email()).build();

        when(principalFactory.createLocalPrincipal(user, registrationRequest))
                .thenReturn(new LocalPrincipal(user, registrationRequest.email(), "encodedPassword"));

        // Act
        authService.register(registrationRequest);

        // Assert
        verify(principalFactory).createLocalPrincipal(user, registrationRequest);
        verify(userRepository).save(user);
        verify(localPrincipalRepository).save(any(LocalPrincipal.class));
        verify(authManagementService).createAndSendVerificationToken(any(LocalPrincipal.class));
    }

    @DisplayName("Should authenticate and return JWT when given valid credentials")
    @Test
    void givenValidCredentials_whenAuthenticate_thenReturnJwtAndUserInfo() {
        // Arrange
        User user = getUser();

        String email = user.getEmail();
        String password = "fdsfsd";
        String expectedJwt = "xxx.yyy.zzz";
        LocalAuthRequest localAuthRequest = new LocalAuthRequest(email, password);
        LocalPrincipal principal =
                LocalPrincipal.builder().user(user).verified(true).build();
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(localPrincipalRepository.findByEmail(email)).thenReturn(Optional.of(principal));
        when(tokenProvider.createToken(any(Authentication.class))).thenReturn(expectedJwt);

        // Act
        JwtAuthResponse result = authService.login(localAuthRequest);

        // Assert
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(profileService).updateLoginStreak(any(Profile.class));
        verify(tokenProvider).createToken(any(Authentication.class));
        assertThat(result.accessToken()).isEqualTo(expectedJwt);
    }

    @DisplayName("Should throw an exception when trying to register user with existing email")
    @Test
    void givenExistingUserEmail_whenRegister_thenThrowUserAlreadyExistsAuthenticationException() {
        LocalAuthRequest registrationRequest = new LocalAuthRequest("johnwick@gmail.com", "password");

        when(userRepository.existsByEmail(registrationRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registrationRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByEmail(registrationRequest.email());
        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).flush();
    }

    @DisplayName("Should throw an exception when email is not verified during login")
    @Test
    void givenUnverifiedEmail_whenLogin_thenThrowEmailNotVerifiedException() {
        // Arrange
        LocalAuthRequest localAuthRequest = TestDataGenerator.createLocalAuthRequest();
        LocalPrincipal principal = LocalPrincipal.builder()
                .email(localAuthRequest.email())
                .password("encodedPassword")
                .verified(false)
                .build();
        when(localPrincipalRepository.findByEmail(localAuthRequest.email())).thenReturn(Optional.of(principal));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(localAuthRequest))
                .isInstanceOf(EmailNotVerifiedException.class)
                .hasMessage("Email needs to be verified before logging in.");

        verify(profileService, never()).updateLoginStreak(any(Profile.class));
        verify(tokenProvider, never()).createToken(any(Authentication.class));
    }

    @DisplayName("Should throw exception when token is invalid")
    @Test
    void givenInvalidToken_whenVerifyEmail_thenThrowInvalidTokenException() {
        // Arrange
        String token = "invalidToken";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.verifyEmail(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid verification token");

        verify(verificationTokenRepository, never()).delete(any(VerificationToken.class));
    }

    @DisplayName("Should throw exception when token is expired")
    @Test
    void givenExpiredToken_whenVerifyEmail_thenThrowInvalidTokenException() {
        // Arrange
        String token = "expiredToken";
        LocalPrincipal principal = TestDataGenerator.buildTestLocalPrincipal();
        VerificationToken verificationToken = new VerificationToken(principal, token, 60);
        verificationToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act & Assert
        assertThatThrownBy(() -> authService.verifyEmail(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Verification token has expired");

        verify(verificationTokenRepository).delete(verificationToken);
        verify(localPrincipalRepository, never()).save(any(LocalPrincipal.class));
    }

    @DisplayName("Should verify email successfully")
    @Test
    void givenValidToken_whenVerifyEmail_thenSetVerifiedAndDeleteToken() {
        // Arrange
        String token = "validToken";
        LocalPrincipal principal = TestDataGenerator.buildTestLocalPrincipal();
        VerificationToken verificationToken = new VerificationToken(principal, token, 60);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act
        authService.verifyEmail(token);

        // Assert
        assertThat(principal.isVerified()).isTrue();
        verify(localPrincipalRepository).save(principal);
        verify(verificationTokenRepository).delete(verificationToken);
    }

    @DisplayName("Should request password reset successfully")
    @Test
    void givenValidEmail_whenRequestPasswordReset_thenSendVerificationToken() {
        // Arrange
        String email = "test@example.com";
        LocalPrincipal principal = TestDataGenerator.buildTestLocalPrincipal();
        when(localPrincipalRepository.findByEmail(email)).thenReturn(Optional.of(principal));

        // Act
        authService.requestPasswordReset(email);

        // Assert
        verify(localPrincipalRepository).findByEmail(email);
        verify(authManagementService).createAndSendVerificationToken(principal);
    }

    @DisplayName("Should throw exception when email is not found for password reset request")
    @Test
    void givenInvalidEmail_whenRequestPasswordReset_thenThrowUsernameNotFoundException() {
        // Arrange
        String email = "invalid@example.com";
        when(localPrincipalRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.requestPasswordReset(email))
                .isInstanceOf(EmailNotFoundException.class)
                .hasMessage("Invalid email");

        verify(localPrincipalRepository).findByEmail(email);
        verify(authManagementService, never()).createAndSendVerificationToken(any(LocalPrincipal.class));
    }

    @DisplayName("Should reset password successfully")
    @Test
    void givenValidTokenAndNewPassword_whenResetPassword_thenUpdatePasswordAndDeleteToken() {
        // Arrange
        String token = "validToken";
        String newPassword = "newPassword123";
        String encodedPassword = "2b$encodedPassword";
        LocalPrincipal principal = TestDataGenerator.buildTestLocalPrincipal();
        VerificationToken verificationToken = new VerificationToken(principal, token, 60);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(principalFactory.encodePassword(newPassword)).thenReturn(encodedPassword);

        // Act
        authService.resetPassword(token, newPassword);

        // Assert
        assertThat(principal.getPassword()).isEqualTo(encodedPassword);
        verify(localPrincipalRepository).save(principal);
        verify(verificationTokenRepository).delete(verificationToken);
    }

    @DisplayName("Should throw exception when token is invalid for password reset")
    @Test
    void givenInvalidToken_whenResetPassword_thenThrowInvalidTokenException() {
        // Arrange
        String token = "invalidToken";
        String newPassword = "newPassword123";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.resetPassword(token, newPassword))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid verification token");

        verify(localPrincipalRepository, never()).save(any(LocalPrincipal.class));
        verify(verificationTokenRepository, never()).delete(any(VerificationToken.class));
    }

    @DisplayName("Should throw exception when token is expired for password reset")
    @Test
    void givenExpiredToken_whenResetPassword_thenThrowInvalidTokenException() {
        // Arrange
        String token = "expiredToken";
        String newPassword = "newPassword123";
        LocalPrincipal principal = TestDataGenerator.buildTestLocalPrincipal();
        VerificationToken verificationToken = new VerificationToken(principal, token, 60);
        verificationToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // Act & Assert
        assertThatThrownBy(() -> authService.resetPassword(token, newPassword))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Verification token has expired");

        verify(verificationTokenRepository).delete(verificationToken);
        verify(localPrincipalRepository, never()).save(any(LocalPrincipal.class));
    }
}
