package Holify.holify.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;   // or username (depends on your flow)
    private String password;
}
