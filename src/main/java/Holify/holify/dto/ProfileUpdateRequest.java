package Holify.holify.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    private String gender;
    private LocalDate dob;
}
