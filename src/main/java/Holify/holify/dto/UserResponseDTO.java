package Holify.holify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String gender;
    private LocalDate dob;
    private Boolean verified;
}
