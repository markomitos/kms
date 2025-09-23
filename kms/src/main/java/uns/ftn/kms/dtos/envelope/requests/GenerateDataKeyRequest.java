package uns.ftn.kms.dtos.envelope.requests;

import lombok.Data;

@Data
public class GenerateDataKeyRequest {
    private String alias;
    private String algorithm;
}
