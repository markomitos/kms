package uns.ftn.kms.dtos.envelope.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecryptDataKeyResponse {
    private String plaintextKeyBase64; // Dekriptovani data ključ
}