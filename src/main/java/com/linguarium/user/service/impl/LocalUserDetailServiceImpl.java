package com.linguarium.user.service.impl;

import com.linguarium.auth.exception.ResourceNotFoundException;
import com.linguarium.auth.model.LocalUser;
import com.linguarium.user.model.User;
import com.linguarium.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("localUserDetailService")
public class LocalUserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;

    public LocalUserDetailServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " was not found in the database");
        }
        return createLocalUser(user);
    }

    @Transactional
    public LocalUser loadUserById(Long id) {
        User user = userService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return createLocalUser(user);
    }

    private LocalUser createLocalUser(User user) {
        return new LocalUser(
                user.getEmail(),
                user.getPassword(),
                user);
    }
}