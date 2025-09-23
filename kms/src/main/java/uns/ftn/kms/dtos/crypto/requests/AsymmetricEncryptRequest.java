package uns.ftn.kms.dtos.crypto.requests;

import lombok.Data;
@Data
public class AsymmetricEncryptRequest {
    private String alias;
    private String algorithm;
    private String dataBase64;
}