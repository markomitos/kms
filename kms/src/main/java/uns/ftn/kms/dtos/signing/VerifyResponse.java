package uns.ftn.kms.dtos.signing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyResponse {
    private boolean isValid;
}