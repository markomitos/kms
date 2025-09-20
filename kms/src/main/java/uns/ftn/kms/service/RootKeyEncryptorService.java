package uns.ftn.kms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.config.KmsConfig;

import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class RootKeyEncryptorService {
    private final SecretKey rootSecretKey;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;


    public byte[] encrypt(byte[] keyMaterial) {
        try {
            final byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, rootSecretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(keyMaterial);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return byteBuffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    public byte[] decrypt(byte[] encryptedKeyMaterial) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedKeyMaterial);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, rootSecretKey, parameterSpec);

            return cipher.doFinal(cipherText);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }
}
