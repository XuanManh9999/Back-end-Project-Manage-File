package com.back_end_TN.project_tn.services;

import com.back_end_TN.project_tn.dtos.request.SignInRequest;
import com.back_end_TN.project_tn.dtos.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signInRequest);
    TokenResponse refresh(HttpServletRequest request);
}
