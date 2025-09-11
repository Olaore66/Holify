package Holify.holify.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String purpose;
}
