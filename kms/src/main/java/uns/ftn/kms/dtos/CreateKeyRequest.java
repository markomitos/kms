package uns.ftn.kms.dtos;

import lombok.Data;
import uns.ftn.kms.models.KeyType;

@Data
public class CreateKeyRequest {
    private KeyType keyType; // Očekuje "SYMMETRIC_AES" ili "ASYMMETRIC_RSA"
    private int keySize;
}