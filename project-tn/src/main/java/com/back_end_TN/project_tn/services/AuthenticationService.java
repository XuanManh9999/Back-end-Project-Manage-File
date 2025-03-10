package com.back_end_TN.project_tn.services;

import com.back_end_TN.project_tn.dtos.request.ForgotPasswordRequest;
import com.back_end_TN.project_tn.dtos.request.RegisterRequest;
import com.back_end_TN.project_tn.dtos.request.SignInRequest;
import com.back_end_TN.project_tn.dtos.request.VerifyOtpRequest;
import com.back_end_TN.project_tn.dtos.response.CommonResponse;
import com.back_end_TN.project_tn.dtos.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signInRequest);
    TokenResponse refresh(HttpServletRequest request);
    CommonResponse registerUser(RegisterRequest registerRequest);
    CommonResponse forgotPassworrdUser(ForgotPasswordRequest forgotPasswordRequest);
    ResponseEntity<CommonResponse> verifyOtpForgotPassword(VerifyOtpRequest verifyOtpRequest);
    ResponseEntity<CommonResponse> verifyOtpRegisteredUser(VerifyOtpRequest verifyOtpRequest);


}
