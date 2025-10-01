package uns.ftn.kms.dtos.hmac;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GenerateHmacResponse {
    private UUID keyId;
    private String algorithm;
    private String hmacBase64;
}