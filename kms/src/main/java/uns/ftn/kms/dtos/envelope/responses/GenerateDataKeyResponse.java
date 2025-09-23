package uns.ftn.kms.dtos.envelope.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDataKeyResponse {
    private String plaintextKeyBase64;
    private String encryptedKeyBase64;
}