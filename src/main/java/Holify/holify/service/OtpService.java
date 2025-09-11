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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService; // ✅ your existing mail sender
    private final JwtUtil jwtUtil;
    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // 6-digit
        return String.valueOf(otp);
    }

    @Transactional
    public ApiResponse<String> sendOtp(OtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return new ApiResponse<>(false, "User not found with this email", "USER_NOT_FOUND", null);
        }

        // cast into an enum class
        OtpPurpose purpose = OtpPurpose.valueOf(request.getPurpose());
        // clear any existing OTP for this email & purpose
        otpTokenRepository.deleteByEmailAndPurpose(request.getEmail(), purpose);

        // generate OTP
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        // save in DB
        OtpToken otpToken = OtpToken.builder()
                .email(request.getEmail())
                .otp(otp)
                .purpose(purpose)
                .expiryTime(expiry)
                .build();
        otpTokenRepository.saveAndFlush(otpToken);

        // send via email
        mailService.sendMail(
                request.getEmail(),
                "Your OTP Code",
                "Your OTP is: " + otp + "\nIt will expire in 15 minutes."
        );

        return new ApiResponse<>(true, "OTP sent successfully for " + request.getPurpose(), null, null);
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
