package uns.ftn.kms.dtos.hmac;

import lombok.Data;
import java.util.UUID;

@Data
public class GenerateHmacRequest {
    private UUID keyId;
    private String algorithm;
    private String dataBase64;
}