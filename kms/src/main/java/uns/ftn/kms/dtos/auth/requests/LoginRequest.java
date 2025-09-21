package uns.ftn.kms.dtos.auth.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
