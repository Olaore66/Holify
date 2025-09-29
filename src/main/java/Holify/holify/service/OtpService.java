package Holify.holify.service;

import Holify.holify.ENUMS.OtpPurpose;
import Holify.holify.entity.OtpToken;
import Holify.holify.entity.User;
import Holify.holify.repository.OtpTokenRepository;
import Holify.holify.repository.UserRepository;
import Holify.holify.dto.OtpRequest;
import Holify.holify.dto.VerifyOtpRequest;
import Holify.holify.dto.ApiResponse;
import Holify.holify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService; // ✅ your existing mail sender
    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // 6-digit
        return String.valueOf(otp);
    }

    @Async
    @Transactional
    public CompletableFuture<ApiResponse<String>> sendOtp(OtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return CompletableFuture.completedFuture(
                    new ApiResponse<>(false, "User not found with this email", "USER_NOT_FOUND", null)
            );
        }

        // Cast into an enum class
        OtpPurpose purpose;
        try {
            purpose = OtpPurpose.valueOf(request.getPurpose().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid OTP purpose: {}", request.getPurpose(), e);
            return CompletableFuture.completedFuture(
                    new ApiResponse<>(false, "Invalid OTP purpose", "INVALID_PURPOSE", null)
            );
        }

        // Clear any existing OTP for this email & purpose
        otpTokenRepository.deleteByEmailAndPurpose(request.getEmail(), purpose);

        // Generate OTP
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        // Save in DB
        OtpToken otpToken = OtpToken.builder()
                .email(request.getEmail())
                .otp(otp)
                .purpose(purpose)
                .expiryTime(expiry)
                .build();
        otpTokenRepository.save(otpToken); // Changed to save for async compatibility

        // Send via email
        try {
            mailService.sendMail(
                    request.getEmail(),
                    "Your OTP Code",
                    "Your OTP is: " + otp + "\nIt will expire in 15 minutes."
            );
        } catch (Exception e) {
            log.error("❌ Email send failed", e);
            return CompletableFuture.completedFuture(
                    new ApiResponse<>(false, "Failed to send OTP email", "EMAIL_SEND_FAILED", null)
            );
        }

        return CompletableFuture.completedFuture(
                new ApiResponse<>(true, "OTP sent successfully for " + request.getPurpose(), null, null)
        );
    }
    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        OtpPurpose purpose = OtpPurpose.valueOf(request.getPurpose());
        Optional<OtpToken> otpOpt = otpTokenRepository.findByEmailAndOtpAndPurpose(
                request.getEmail(), request.getOtp(), purpose
        );

        if (otpOpt.isEmpty()) {
            return new ApiResponse<>(false, "Invalid OTP", "INVALID_OTP", null);
        }

        OtpToken otpToken = otpOpt.get();
        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(false, "OTP expired", "OTP_EXPIRED", null);
        }

        // cleanup
        otpTokenRepository.delete(otpToken);

        if ("LOGIN".equalsIgnoreCase(request.getPurpose())) {
            // 🔑 Generate normal JWT for login
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String jwtToken = jwtUtil.generateToken(user.getUsername(), 0);
            return new ApiResponse<>(true, "OTP verified successfully (Login)", null, jwtToken);
        }

        if ("RESET_PASSWORD".equalsIgnoreCase(request.getPurpose())) {
            // 🔑 Generate short-lived reset token
            String resetToken = jwtUtil.generateToken(request.getEmail(), 10 * 60 * 1000);
            return new ApiResponse<>(true, "OTP verified successfully (Reset Password)", null, resetToken);
        }

        if ("VERIFY_EMAIL".equalsIgnoreCase(request.getPurpose())) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setVerified(true);
            userRepository.save(user);

            return new ApiResponse<>(true, "Email verified successfully", null, null);
        }


        return new ApiResponse<>(false, "Unknown purpose", "UNKNOWN_PURPOSE", null);



    }


//    @Transactional
//    public ApiResponse verifyOtp(VerifyOtpRequest request) {
//        Optional<OtpToken> otpOpt = otpTokenRepository.findByEmailAndOtpAndPurpose(
//                request.getEmail(), request.getOtp(), request.getPurpose()
//        );
//
//        if (otpOpt.isEmpty()) {
//            return new ApiResponse("Invalid OTP");
//        }
//
//        OtpToken otpToken = otpOpt.get();
//        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
//            return new ApiResponse("OTP expired");
//        }
//
//        // cleanup
//        otpTokenRepository.delete(otpToken);
//
//        if ("LOGIN".equalsIgnoreCase(request.getPurpose())) {
//            // 🔑 Generate normal JWT for login
//            User user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            String jwtToken = jwtUtil.generateToken(user.getUsername(), 0);
//            return new ApiResponse("OTP verified successfully (Login)", jwtToken);
//        }
//
//        if ("RESET_PASSWORD".equalsIgnoreCase(request.getPurpose())) {
//            // 🔑 Generate short-lived reset token
//            String resetToken = jwtUtil.generateToken(request.getEmail(), 10 * 60 * 1000);
//            return new ApiResponse("OTP verified successfully (Reset Password)", resetToken);
//        }
//
//        return new ApiResponse("Unknown purpose");
//    }
}
