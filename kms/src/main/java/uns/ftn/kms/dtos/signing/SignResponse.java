package uns.ftn.kms.dtos.signing;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SignResponse {
    private UUID keyId;
    private String algorithm;
    private String signatureBase64;
}