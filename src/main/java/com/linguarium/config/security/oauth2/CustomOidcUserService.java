package com.linguarium.config.security.oauth2;

import static lombok.AccessLevel.PRIVATE;

import com.linguarium.auth.exception.OAuth2AuthenticationProcessingException;
import com.linguarium.user.service.AuthService;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomOidcUserService extends OidcUserService {
    AuthService authService;

    public CustomOidcUserService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        try {
            return authService.processProviderAuth(
                    userRequest.getClientRegistration().getRegistrationId().toUpperCase(),
                    oidcUser.getAttributes(),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo());
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex.getCause());
        }
    }
}
