package uns.ftn.kms.dtos.auth.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
}
