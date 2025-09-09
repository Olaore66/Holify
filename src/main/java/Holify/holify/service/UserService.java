package Holify.holify.service;

import Holify.holify.dto.*;
import Holify.holify.entity.User;
import Holify.holify.entity.VerificationToken;
import Holify.holify.repository.UserRepository;
import Holify.holify.repository.VerificationTokenRepository;
import Holify.holify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtUtil jwtUtil;


    @Transactional
    public ApiResponse registerUser(UserRegistrationRequest request) {
        // check if email already exists
//        if (userRepository.existsByEmail(request.getEmail())) {
//            return new ApiResponse("Email already registered");
//        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ApiResponse("Username already exists: " + request.getUsername());
        }

        // Optionally check if email already exists too
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ApiResponse("Email already exists: " + request.getEmail());
        }

        // create new user
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false)
                .build();

        userRepository.save(user);

        // generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24)) // valid for 24h
                .build();
        tokenRepository.save(verificationToken);

        // send email
        String link = "https://holify.onrender.com/api/auth/verify?token=" + token;
        mailService.sendMail(
                user.getEmail(),
                "Verify your Holify account",
                "Click the link to verify your account: " + link
        );

        return new ApiResponse("User registered. Please check your email to verify your account.");
    }

    @Transactional
    public ApiResponse verifyUser(String token) {
        Optional<VerificationToken> optional = tokenRepository.findByToken(token);

        if (optional.isEmpty()) {
            return new ApiResponse("Invalid or expired verification link.");
        }

        VerificationToken verificationToken = optional.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse("Verification link has expired.");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return new ApiResponse("Account verified successfully!", user.getId());
    }

    @Transactional
    public ApiResponse<User> completeProfile(Long id, ProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setGender(request.getGender());
        user.setDob(request.getDob());

        User updatedUser = userRepository.save(user);

        // Map manually (or with ModelMapper if you like)
        UserResponseDTO responseDTO = new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getFirstname(),
                updatedUser.getLastname(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getGender(),
                updatedUser.getDob(),
                updatedUser.getVerified()
        );
        return new ApiResponse(
                "Profile updated successfully!",
                responseDTO
        );
    }

    public LoginResponse login(LoginRequest request) {
        // find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // ensure verified
        if (!user.getVerified()) {
            throw new RuntimeException("Please verify your email before login");
        }

        // generate token
        String token = jwtUtil.generateToken(user.getEmail());

        // build response DTO
        UserResponseDTO userDTO = new UserResponseDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getVerified()
        );

        return new LoginResponse(token, userDTO);
    }

}
