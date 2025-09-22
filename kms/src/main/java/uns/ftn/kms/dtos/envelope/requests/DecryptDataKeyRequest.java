package uns.ftn.kms.dtos.envelope.requests;

import lombok.Data;

@Data
public class DecryptDataKeyRequest {
    private String alias; // Alias Root ključa koji je korišćen
    private String encryptedKeyBase64; // Enkriptovani data ključ koji treba dekriptovati
}