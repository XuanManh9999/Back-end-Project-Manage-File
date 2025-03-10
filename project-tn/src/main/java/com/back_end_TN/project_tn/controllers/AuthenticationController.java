package com.back_end_TN.project_tn.controllers;

import com.back_end_TN.project_tn.dtos.request.ForgotPasswordRequest;
import com.back_end_TN.project_tn.dtos.request.RegisterRequest;
import com.back_end_TN.project_tn.dtos.request.SignInRequest;
import com.back_end_TN.project_tn.dtos.request.VerifyOtpRequest;
import com.back_end_TN.project_tn.dtos.response.CommonResponse;
import com.back_end_TN.project_tn.dtos.response.TokenResponse;
import com.back_end_TN.project_tn.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final AuthenticationService authenticationService;

    @PostMapping("/access")
    public ResponseEntity<TokenResponse> login (@RequestBody SignInRequest signInRequest)  {
        return new ResponseEntity<>(authenticationService.authenticate(signInRequest), HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh (HttpServletRequest request) {
        return new ResponseEntity<>(authenticationService.refresh(request), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> register (@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(registerRequest));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CommonResponse> forgotPassword (@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.forgotPassworrdUser(forgotPasswordRequest));
    }

    @PostMapping("/verify-otp-register")
    public ResponseEntity<CommonResponse> verifyOtpRegister(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        return authenticationService.verifyOtpRegisteredUser(verifyOtpRequest);
    }

    @PostMapping("/verify-otp-forgot-password")
    public ResponseEntity<CommonResponse> verifyOtpForgotPassword(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        return authenticationService.verifyOtpForgotPassword(verifyOtpRequest);
    }


}
