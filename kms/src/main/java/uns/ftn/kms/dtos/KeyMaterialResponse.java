package uns.ftn.kms.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class KeyMaterialResponse {
    private UUID keyId;
    private String keyMaterialBase64;

    public KeyMaterialResponse(UUID keyId, String keyMaterialBase64) {
        this.keyId = keyId;
        this.keyMaterialBase64 = keyMaterialBase64;
    }
}