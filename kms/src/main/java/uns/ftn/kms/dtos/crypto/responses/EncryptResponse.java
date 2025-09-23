package uns.ftn.kms.dtos.crypto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptResponse {
    private String dataBase64;
}