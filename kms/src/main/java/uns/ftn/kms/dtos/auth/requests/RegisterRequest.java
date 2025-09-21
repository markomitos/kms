package uns.ftn.kms.dtos.auth.requests;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}
