package uns.ftn.kms.dtos.hmac;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyHmacResponse {
    private boolean isValid;
}