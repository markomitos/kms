package uns.ftn.kms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.annotations.KmsAuditLog;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HmacService {

    private final IKeyService keyService;

    @KmsAuditLog(action = "GENERATE_HMAC")
    public byte[] generate(UUID keyId, UUID userId, String algorithm, byte[] data) throws Exception {

        byte[] keyMaterial = keyService.getActiveSymmetricKeyMaterial(keyId, userId);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyMaterial, algorithm);

        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    @KmsAuditLog(action = "VERIFY_HMAC")
    public boolean verify(UUID keyId, UUID userId, String algorithm, byte[] data, byte[] hmacToVerify) throws Exception {

        byte[] expectedHmac = this.generate(keyId, userId, algorithm, data);

        return MessageDigest.isEqual(expectedHmac, hmacToVerify); //secure way to check, resistant on time attacks
    }
}