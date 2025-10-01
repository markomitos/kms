package uns.ftn.kms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.annotations.KmsAuditLog;
import uns.ftn.kms.exceptions.EntityNotFoundException;
import uns.ftn.kms.models.Key;
import uns.ftn.kms.models.KeyType;
import uns.ftn.kms.models.KeyVersion;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SigningService {

    private final IKeyService keyService;

    @KmsAuditLog(action = "SIGN_DATA")
    public byte[] sign(UUID keyId, UUID userId, String algorithm, byte[] dataToSign) throws Exception {
        byte[] decryptedPrivateKeyBytes = keyService.getActivePrivateKeyMaterial(keyId, userId);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(dataToSign);

        return signature.sign();
    }

    @KmsAuditLog(action = "VERIFY_SIGNATURE")
    public boolean verify(UUID keyId, UUID userId, String algorithm, byte[] originalData, byte[] signatureToVerify) throws Exception {
        Key key = keyService.findKeyById(keyId, userId);

        for (KeyVersion version : key.getVersions()) {
            try {
                byte[] publicKeyBytes = version.getPublicKeyMaterial();
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(keySpec);

                Signature signature = Signature.getInstance(algorithm);
                signature.initVerify(publicKey);
                signature.update(originalData);

                if (signature.verify(signatureToVerify)) {
                    return true;
                }
            } catch (Exception e) {
                // ignore error and try next key version
            }
        }

        return false;
    }
}