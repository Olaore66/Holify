package Holify.holify.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
}
