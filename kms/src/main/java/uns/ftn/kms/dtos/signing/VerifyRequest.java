package uns.ftn.kms.dtos.signing;

import lombok.Data;
import java.util.UUID;

@Data
public class VerifyRequest {
    private UUID keyId;
    private String algorithm;
    private String originalDataBase64;
    private String signatureBase64;
}