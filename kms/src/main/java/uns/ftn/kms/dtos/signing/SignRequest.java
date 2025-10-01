package uns.ftn.kms.dtos.signing;

import lombok.Data;
import java.util.UUID;

@Data
public class SignRequest {
    private UUID keyId;
    private String algorithm; // Npr. "SHA256withRSA"
    private String dataToSignBase64;
}