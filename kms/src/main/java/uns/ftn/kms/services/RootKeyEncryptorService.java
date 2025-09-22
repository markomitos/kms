package uns.ftn.kms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uns.ftn.kms.exceptions.EntityNotFoundException;
import uns.ftn.kms.repositories.IRootKeyRepository;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor // Lombok će kreirati konstruktor za final polja
@Service
public class RootKeyEncryptorService {

    @Qualifier("keyStoreRootKeyRepository")
    private final IRootKeyRepository rootKeyRepository;

    private final SecureRandom secureRandom = new SecureRandom();
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;


    public byte[] encrypt(byte[] keyMaterial, UUID userId) {
        try {
            SecretKey userKey = rootKeyRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Root key not found for user ID: " + userId));

            final byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.ENCRYPT_MODE, userKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(keyMaterial);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return byteBuffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data for user " + userId, e);
        }
    }

    public byte[] decrypt(byte[] encryptedKeyMaterial, UUID userId) {
        try {
            SecretKey userKey = rootKeyRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Root key not found for user ID: " + userId));

            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedKeyMaterial);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

            cipher.init(Cipher.DECRYPT_MODE, userKey, parameterSpec);

            return cipher.doFinal(cipherText);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data for user " + userId, e);
        }
    }
}