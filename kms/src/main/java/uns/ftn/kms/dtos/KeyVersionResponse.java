package uns.ftn.kms.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeyVersionResponse {
    private Long id;
    private int version;
    private byte[] encryptedKeyMaterial;
    private LocalDateTime createdAt;
    private boolean enabled;
}
