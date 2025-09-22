package uns.ftn.kms.dtos.envelope.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDataKeyResponse {
    // Novi data ključ u plaintext obliku, za jednokratnu upotrebu
    private String plaintextKeyBase64;
    // Isti data ključ, ali enkriptovan Root ključem, za čuvanje
    private String encryptedKeyBase64;
}