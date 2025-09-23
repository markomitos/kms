package uns.ftn.kms.dtos.crypto.requests;

import lombok.Data;
@Data
public class AsymmetricDecryptRequest {
    private String alias;
    private String dataBase64;
}
