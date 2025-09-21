package uns.ftn.kms.dtos.auth.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private String token;
}
