package uns.ftn.kms.dtos;

import lombok.Data;
import uns.ftn.kms.models.KeyType;

@Data
public class CreateKeyRequest {
    private KeyType keyType;
    private int keySize;
}