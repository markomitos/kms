package uns.ftn.kms.dtos.envelope.requests;

import lombok.Data;

@Data
public class DecryptDataKeyRequest {
    private String alias;
    private String encryptedKeyBase64;
}