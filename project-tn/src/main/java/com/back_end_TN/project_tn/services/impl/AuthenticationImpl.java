package com.back_end_TN.project_tn.services.impl;

import com.back_end_TN.project_tn.dtos.request.SignInRequest;
import com.back_end_TN.project_tn.dtos.response.TokenResponse;
import com.back_end_TN.project_tn.exceptions.customs.InvalidDataNotFound;
import com.back_end_TN.project_tn.services.AuthenticationService;
import com.back_end_TN.project_tn.services.JwtService;
import com.back_end_TN.project_tn.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.back_end_TN.project_tn.enums.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    @Override
    public TokenResponse authenticate(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        var user = userService.findUserByUsername(signInRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User or password is not found "));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateTokenRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refresh(HttpServletRequest request) {
        // validate
        String token = request.getHeader("x-token");
        if (StringUtils.isBlank(token)) {
            log.error("Token must be not blank");
            return null;
        }
        // extract user for token
        final String username = jwtService.extractUsername(token, REFRESH_TOKEN);


        // check it into db
        var user = userService.findUserByUsername(username).orElseThrow(() -> new InvalidDataNotFound("User not found"));
        if (!jwtService.isValid(token, REFRESH_TOKEN, user)) {
            throw new InvalidDataNotFound("Refresh token is invalid");
        }
        String assessToken = jwtService.generateAccessToken(user);
        return TokenResponse.builder()
                .accessToken(assessToken)
                .refreshToken(token)
                .userId(user.getId())
                .build();
    }
}
