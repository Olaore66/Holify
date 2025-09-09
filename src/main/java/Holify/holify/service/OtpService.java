package Holify.holify.service;

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

    // ✅ 1. Generate and send OTP
    @Transactional
    public ApiResponse sendOtp(OtpRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return new ApiResponse("User not found with this email");
        }

        // clear any existing OTP for this email
        otpTokenRepository.deleteByEmail(request.getEmail());

        // generate OTP
        String otp = generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        // save in DB
        OtpToken otpToken = OtpToken.builder()
                .email(request.getEmail())
                .otp(otp)
                .expiryTime(expiry)
                .build();
        otpTokenRepository.save(otpToken);

        // send via email
        mailService.sendMail(
                request.getEmail(),
                "Your OTP Code",
                "Your OTP is: " + otp + "\nIt will expire in 15 minutes."
        );

        return new ApiResponse("OTP sent successfully to " + request.getEmail());
    }

    // ✅ 2. Verify OTP
    @Transactional
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        Optional<OtpToken> otpOpt = otpTokenRepository.findByEmailAndOtp(request.getEmail(), request.getOtp());
        if (otpOpt.isEmpty()) {
            return new ApiResponse("Invalid OTP");
        }

        OtpToken otpToken = otpOpt.get();
        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse("OTP expired");
        }

        // ✅ OTP is valid → cleanup
        otpTokenRepository.delete(otpToken);

        // 🔑 Get user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔑 Extract username
        String username = user.getUsername();

        // ✅ Reuse existing JWT util
        String jwtToken = jwtUtil.generateToken(username); // or however your util works


        // TODO: Issue JWT or mark user as logged in if the otp was requested at login
        return new ApiResponse("OTP verified successfully. You can now log in.", jwtToken);
    }
}
