package com.back_end_TN.project_tn.services.impl;

import com.back_end_TN.project_tn.components.EmailSending;
import com.back_end_TN.project_tn.components.RandomStringGenerator;
import com.back_end_TN.project_tn.dtos.request.ForgotPasswordRequest;
import com.back_end_TN.project_tn.dtos.request.RegisterRequest;
import com.back_end_TN.project_tn.dtos.request.SignInRequest;
import com.back_end_TN.project_tn.dtos.request.VerifyOtpRequest;
import com.back_end_TN.project_tn.dtos.response.CommonResponse;
import com.back_end_TN.project_tn.dtos.response.TokenResponse;
import com.back_end_TN.project_tn.entitys.OtpEntity;
import com.back_end_TN.project_tn.entitys.UserEntity;
import com.back_end_TN.project_tn.enums.Active;
import com.back_end_TN.project_tn.exceptions.customs.DuplicateResourceException;
import com.back_end_TN.project_tn.exceptions.customs.InvalidDataNotFound;
import com.back_end_TN.project_tn.exceptions.customs.NotFoundException;
import com.back_end_TN.project_tn.repositorys.OtpRepository;
import com.back_end_TN.project_tn.repositorys.UserEntityRepository;
import com.back_end_TN.project_tn.services.AuthenticationService;
import com.back_end_TN.project_tn.services.JwtService;
import com.back_end_TN.project_tn.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.jaxb.cfg.spi.ObjectFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.back_end_TN.project_tn.enums.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserEntityRepository userEntityRepository;
    private final EmailSending emailSending;
    private final RandomStringGenerator randomStringGenerator;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;


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

    @Override
    public CommonResponse registerUser(RegisterRequest registerRequest) {
        try {
            Optional<UserEntity> userEntity = userEntityRepository.findByUsernameAndEmail(registerRequest.getUserName(), registerRequest.getEmail());
            if (userEntity.isPresent()) {
                throw new DuplicateResourceException("User already exists");
            }
            UserEntity user = new UserEntity();
            user.setUsername(registerRequest.getUserName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setActive(Active.CHUA_HOAT_DONG);
            userEntityRepository.save(user);
            String encode = randomStringGenerator.generateRandomString(6);
            emailSending.sendEmail(registerRequest.getEmail(), "IMGBB OTP", encode);
            return CommonResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("User registered successfully")
                    .build();
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public CommonResponse forgotPassworrdUser(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Optional<UserEntity> user = userEntityRepository.findByEmail(forgotPasswordRequest.getEmail());
            if (user.isPresent()) {
                String encode = randomStringGenerator.generateRandomString(6);

                OtpEntity otp = new OtpEntity();
                otp.setEmail(forgotPasswordRequest.getEmail());
                otp.setOtpCode(encode);
                otp.setExpiresAt(now.plusSeconds(120));
                otpRepository.save(otp);
                emailSending.sendEmail(forgotPasswordRequest.getEmail(), "IMGBB OTP", encode);
                return CommonResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Forgot password otp successfully")
                        .build();
            }else {
                throw new NotFoundException("User not found");
            }
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ResponseEntity<CommonResponse> verifyOtpForgotPassword(VerifyOtpRequest verifyOtpRequest) {
        try {
            boolean isCheckOtp = verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
            if (isCheckOtp) {
                Optional<UserEntity> user = userEntityRepository.findByEmail(verifyOtpRequest.getEmail());
                if (user.isPresent()) {
                    String passwordNew = randomStringGenerator.generateRandomString(10);
                    user.get().setPassword(passwordEncoder.encode(passwordNew));
                    userEntityRepository.save(user.get());
                    emailSending.sendEmail(verifyOtpRequest.getEmail(), "New Password", passwordNew);
                    return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                            .status(HttpStatus.OK.value())
                            .message("Verify OTP successfully")
                            .build());
                }else {
                    throw new NotFoundException("User not found");
                }

            }else {
                return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Verify OTP failed")
                        .build());
            }
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ResponseEntity<CommonResponse> verifyOtpRegisteredUser(VerifyOtpRequest verifyOtpRequest) {
        try {
            boolean isCheckOtp = verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
            if (isCheckOtp) {
                Optional<UserEntity> user = userEntityRepository.findUserEntityByEmailAndActive(verifyOtpRequest.getEmail(), Active.HOAT_DONG);
                if (user.isPresent()) {
                    user.get().setActive(Active.HOAT_DONG);
                    userEntityRepository.save(user.get());
                    return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                            .status(HttpStatus.OK.value())
                            .message("Verify OTP successfully")
                            .build());
                }else {
                    throw new NotFoundException("User not found");
                }
            }else {
                return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Verify OTP failed")
                        .build());
            }
        }catch (Exception e) {
            throw e;
        }
    }


    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByEmail(email);

        if (otpEntityOpt.isPresent()) {
            OtpEntity otpEntity = otpEntityOpt.get();

            // Kiểm tra nếu OTP đã hết hạn
            if (LocalDateTime.now().isAfter(otpEntity.getExpiresAt())) {
                otpRepository.delete(otpEntity); // Xóa OTP hết hạn
                return false; // OTP hết hạn
            }

            // Kiểm tra nếu OTP khớp
            if (otpEntity.getOtpCode().equals(otp)) {
                otpRepository.delete(otpEntity); // Xóa OTP sau khi xác minh thành công
                return true;
            }
        }
        return false; // OTP không hợp lệ hoặc không tồn tại
    }
}
