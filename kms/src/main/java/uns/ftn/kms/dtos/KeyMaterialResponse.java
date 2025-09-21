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

    public UUID getKeyId() {
        return keyId;
    }

    public void setKeyId(UUID keyId) {
        this.keyId = keyId;
    }

    public String getKeyMaterialBase64() {
        return keyMaterialBase64;
    }

    public void setKeyMaterialBase64(String keyMaterialBase64) {
        this.keyMaterialBase64 = keyMaterialBase64;
    }
}