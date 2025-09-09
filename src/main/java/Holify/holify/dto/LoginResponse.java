package Holify.holify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;          // JWT token
    private UserResponseDTO user;  // return clean user info
}
