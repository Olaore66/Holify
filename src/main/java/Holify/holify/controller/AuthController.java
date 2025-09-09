package Holify.holify.controller;

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
    public ResponseEntity<ApiResponse> completeProfile(
            @PathVariable Long id,
            @RequestBody ProfileUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getVerified())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Please verify your email first."));
        }

        user.setGender(request.getGender());
        user.setDob(request.getDob());
        userRepository.save(user);

        ApiResponse<User> response = userService.completeProfile(id, request);
        return ResponseEntity.ok(response);
//        return ResponseEntity.ok(new ApiResponse("Profile completed successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody OtpRequest request) {
        return ResponseEntity.ok(otpService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

}
