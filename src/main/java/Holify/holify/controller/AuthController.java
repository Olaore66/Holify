package Holify.holify.controller;

import Holify.holify.ENUMS.OtpPurpose;
import Holify.holify.dto.*;
import Holify.holify.entity.User;
import Holify.holify.repository.UserRepository;
import Holify.holify.service.OtpService;
import Holify.holify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService; // no @Autowired
    private final UserRepository userRepository;
    private final OtpService otpService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        ApiResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam("token") String token,
                                                  @RequestParam(value = "redirect", required = false, defaultValue = "false") boolean redirect) {
        ApiResponse response = userService.verifyUser(token);

        if (redirect) {
            // Deep link for mobile app
            String deepLink = "holify://verified?message=" +
                    UriUtils.encode(response.getMessage(), StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, deepLink)
                    .build();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> completeProfile(
            @PathVariable Long id,
            @RequestBody ProfileUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getVerified())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false,
                            "Please verify your email first.",
                            "EMAIL_NOT_VERIFIED",
                            null));
        }

        ApiResponse<UserResponseDTO> response = userService.completeProfile(id, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = userService.login(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Login successful", null, loginResponse)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), "LOGIN_FAILED", null)
            );
        }
    }


    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        // Convert the string from Postman to the enum
        OtpPurpose purpose;
        try {
            purpose = OtpPurpose.valueOf(request.getPurpose()); // request.getPurpose() = "LOGIN"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid OTP purpose: " + request.getPurpose());
        }

        // Call the service to send OTP

        otpService.sendOtp(request);

        return ResponseEntity.ok("OTP sent successfully");
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }


}
