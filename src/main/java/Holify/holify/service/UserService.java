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
import java.util.List;
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
    private final OtpService otpService;


    @Transactional
    public ApiResponse<UserResponseDTO> registerUser(UserRegistrationRequest request) {
        // check username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new ApiResponse<>(false,
                    "Username already exists: " + request.getUsername(),
                    "USERNAME_EXISTS",
                    null);
        }

        // check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ApiResponse<>(false,
                    "Email already exists: " + request.getEmail(),
                    "EMAIL_EXISTS",
                    null);
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

//        // generate verification token
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = VerificationToken.builder()
//                .token(token)
//                .user(user)
//                .expiryDate(LocalDateTime.now().plusHours(24)) // valid for 24h
//                .build();
//        tokenRepository.save(verificationToken);
//
//        // send verification email
//        String link = "https://holify.onrender.com/api/auth/verify?token=" + token;
//        mailService.sendMail(
//                user.getEmail(),
//                "Verify your Holify account",
//                "Click the link to verify your account: " + link
//        );

        // 📩 send OTP for email verification
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(user.getEmail());
        otpRequest.setPurpose("VERIFY_EMAIL");
        otpService.sendOtp(otpRequest);

        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getVerified()
        );

        return new ApiResponse<>(true,
                "User registered. Please check your email to verify your account.",
                null,
                dto);
    }

    @Transactional
    public ApiResponse<UserResponseDTO> verifyUser(String token) {
        Optional<VerificationToken> optional = tokenRepository.findByToken(token);

        if (optional.isEmpty()) {
            return new ApiResponse<>(false,
                    "Invalid or expired verification link.",
                    "INVALID_TOKEN",
                    null);
        }

        VerificationToken verificationToken = optional.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(false,
                    "Verification link has expired.",
                    "TOKEN_EXPIRED",
                    null);
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        UserResponseDTO dto = new UserResponseDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getVerified()
        );

        return new ApiResponse<>(true,
                "Account verified successfully!",
                null,
                dto);
    }

    @Transactional
    public ApiResponse<UserResponseDTO> completeProfile(Long id, ProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setGender(request.getGender());
        user.setDob(request.getDob());

        User updatedUser = userRepository.save(user);

        // Map to DTO
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

        return new ApiResponse<>(true, "Profile updated successfully!", null, responseDTO);
    }

//    public LoginResponse login(LoginRequest request) {
//        // find user
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        // check password
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        // ensure verified
//        if (!user.getVerified()) {
//            throw new RuntimeException("Please verify your email before login");
//        }
//
//        // generate token
//        String token = jwtUtil.generateToken(user.getEmail(), 0);
//
//        // build response DTO
//        UserResponseDTO userDTO = new UserResponseDTO(
//                user.getId(),
//                user.getFirstname(),
//                user.getLastname(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getGender(),
//                user.getDob(),
//                user.getVerified()
//        );
//
//        return new LoginResponse(token, userDTO);
//    }


    public LoginResponse login(LoginRequest request) {
        // Try finding user by email first, then by username
        Optional<User> optionalUser = userRepository.findByEmail(request.getIdentifier())
                .or(() -> userRepository.findByUsername(request.getIdentifier()));

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = optionalUser.get();

        // check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // ensure verified
        if (!user.getVerified()) {
            throw new RuntimeException("Please verify your email before login");
        }

        // generate token
        String token = jwtUtil.generateToken(user.getUsername(), 0);

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

    // SAM REQUESTED FOR CRUD OPERATION
    // ✅ Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Get User by ID
    public ApiResponse<User> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new ApiResponse<>(true, "User fetched successfully", null, user);
        } else {
            return new ApiResponse<>(false, "User not found", "USER_NOT_FOUND", null);
        }
    }




    // ✅ Update User
    public ApiResponse<User> updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ApiResponse<>(false, "User not found", "USER_NOT_FOUND", null);
        }

        User existingUser = optionalUser.get();
        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        // add more fields depending on your User entity

        User saved = userRepository.save(existingUser);
        return new ApiResponse<>(true, "User updated successfully", null, saved);
    }


    // ✅ Delete User
    public ApiResponse<Void> deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ApiResponse<>(false, "User not found", "USER_NOT_FOUND", null);
        }

        userRepository.delete(optionalUser.get());
        return new ApiResponse<>(true, "User deleted successfully", null, null);
    }


    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        String email = jwtUtil.getUsernameFromToken(request.getResetToken());

        if (email == null) {
            return new ApiResponse<>(false, "Invalid reset token", "INVALID_TOKEN", null);
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new ApiResponse<>(false, "User not found", "USER_NOT_FOUND", null);
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse<>(true, "Password reset successful", null, null);
    }

}
