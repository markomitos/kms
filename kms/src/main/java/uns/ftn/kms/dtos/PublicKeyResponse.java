package uns.ftn.kms.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PublicKeyResponse {
    private UUID keyId;
    private String publicKeyBase64;

    public PublicKeyResponse(UUID keyId, String publicKeyBase64) {
        this.keyId = keyId;
        this.publicKeyBase64 = publicKeyBase64;
    }
}