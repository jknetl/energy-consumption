package com.github.jknetl.ec.service;

import com.github.jknetl.ec.data.repository.AppUserRepository;
import com.github.jknetl.ec.rest.dto.LoginRequest;
import com.github.jknetl.ec.rest.dto.LoginResponse;
import com.github.jknetl.ec.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .filter(u -> u.isActive() && passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return new LoginResponse(jwtService.generateToken(user));
    }
}
