package uns.ftn.kms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.exceptions.EntityNotFoundException;
import uns.ftn.kms.models.Key;
import uns.ftn.kms.models.KeyType;
import uns.ftn.kms.models.KeyVersion;
import uns.ftn.kms.models.alghoritms.SymmetricAlgorithm;
import uns.ftn.kms.dtos.envelope.responses.GenerateDataKeyResponse;
import uns.ftn.kms.repositories.KeyRepository;
import uns.ftn.kms.models.alghoritms.AsymmetricAlgorithm;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Comparator;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CryptoService {

    private final KeyRepository keyRepository;
    private final RootKeyEncryptorService rootKeyEncryptor;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int GCM_TAG_LENGTH = 128;

    // --- ENVELOPE METHODS ---

    public GenerateDataKeyResponse generateDataKey(String alias, UUID userId, String algorithmName) throws Exception {
        Key rootKey = keyRepository.findByAliasAndUserId(alias, userId)
                .orElseThrow(() -> new EntityNotFoundException("Root key with alias '" + alias + "' not found or you do not have permission."));

        if (rootKey.getType() != KeyType.SYMMETRIC_AES) {
            throw new IllegalArgumentException("Envelope encryption is only supported with symmetric root keys.");
        }
        byte[] plaintextDataKey = generateAesKeyMaterial();
        byte[] encryptedDataKey = this.encrypt(alias, userId, plaintextDataKey, algorithmName);

        return new GenerateDataKeyResponse(
                Base64.getEncoder().encodeToString(plaintextDataKey),
                Base64.getEncoder().encodeToString(encryptedDataKey)
        );
    }

    public byte[] decryptDataKey(String alias, UUID userId, byte[] encryptedDataKey) throws Exception {
        return this.decrypt(alias, userId, encryptedDataKey);
    }

    // --- ASYMMETRIC METHODS ---

    public byte[] encryptAsymmetric(String alias, UUID userId, byte[] plaintext, String algorithmName) throws Exception {
        Key key = keyRepository.findByAliasAndUserId(alias, userId)
                .orElseThrow(() -> new EntityNotFoundException("Key with alias '" + alias + "' not found or you do not have permission."));

        if (key.getType() != KeyType.ASYMMETRIC_RSA) {
            throw new IllegalArgumentException("This operation requires an asymmetric key.");
        }

        AsymmetricAlgorithm algorithm = AsymmetricAlgorithm.fromName(algorithmName);

        KeyVersion activeVersion = getActiveVersion(key);
        byte[] publicKeyBytes = activeVersion.getPublicKeyMaterial();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        Cipher cipher = Cipher.getInstance(algorithm.getJavaName());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public byte[] decryptAsymmetric(String alias, UUID userId, byte[] ciphertext) throws Exception {
        Key key = keyRepository.findByAliasAndUserId(alias, userId)
                .orElseThrow(() -> new EntityNotFoundException("Key not found or you do not have permission."));

        if (key.getType() != KeyType.ASYMMETRIC_RSA) {
            throw new IllegalArgumentException("This operation requires an asymmetric key.");
        }

        for (KeyVersion version : key.getVersions().stream().sorted(Comparator.comparingInt(KeyVersion::getVersion).reversed()).toList()) {
            try {
                byte[] privateKeyBytes = rootKeyEncryptor.decrypt(version.getEncryptedKeyMaterial(), userId);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = kf.generatePrivate(spec);

                for (AsymmetricAlgorithm algorithm : AsymmetricAlgorithm.values()) {
                    try {
                        Cipher cipher = Cipher.getInstance(algorithm.getJavaName());
                        cipher.init(Cipher.DECRYPT_MODE, privateKey);
                        return cipher.doFinal(ciphertext);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
        throw new RuntimeException("Decryption failed. Data could not be decrypted with any available key version or algorithm.");
    }

    // --- PRIVATE HELPER METHODS ---

    private byte[] generateAesKeyMaterial() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES algorithm not available", e);
        }
    }

    private byte[] encrypt(String alias, UUID userId, byte[] plaintext, String algorithmName) throws Exception {
        Key key = keyRepository.findByAliasAndUserId(alias, userId)
                .orElseThrow(() -> new EntityNotFoundException("Key not found or you do not have permission."));

        SymmetricAlgorithm algorithm = SymmetricAlgorithm.fromName(algorithmName);
        KeyVersion keyVersion = getActiveVersion(key);

        byte[] decryptedKeyMaterial = rootKeyEncryptor.decrypt(keyVersion.getEncryptedKeyMaterial(), userId);
        SecretKey encryptionKey = new SecretKeySpec(decryptedKeyMaterial, 0, decryptedKeyMaterial.length, "AES");

        Cipher cipher = Cipher.getInstance(algorithm.getJavaName());
        byte[] iv;
        AlgorithmParameterSpec parameterSpec;

        if (algorithm == SymmetricAlgorithm.AES_GCM_NOPADDING) {
            iv = new byte[12];
            secureRandom.nextBytes(iv);
            parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        } else if (algorithm == SymmetricAlgorithm.AES_CBC_PKCS5PADDING) {
            iv = new byte[16];
            secureRandom.nextBytes(iv);
            parameterSpec = new IvParameterSpec(iv);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm for encryption: " + algorithm.name());
        }

        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, parameterSpec);
        byte[] cipherText = cipher.doFinal(plaintext);

        ByteBuffer byteBuffer = ByteBuffer.allocate(2 + 4 + iv.length + cipherText.length);
        byteBuffer.putShort(algorithm.getId());
        byteBuffer.putInt(keyVersion.getVersion());
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    private byte[] decrypt(String alias, UUID userId, byte[] ciphertextWithHeader) throws Exception {
        Key key = keyRepository.findByAliasAndUserId(alias, userId)
                .orElseThrow(() -> new EntityNotFoundException("Key not found or you do not have permission."));

        ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertextWithHeader);

        short algorithmId = byteBuffer.getShort();
        SymmetricAlgorithm algorithm = SymmetricAlgorithm.fromId(algorithmId);
        int keyVersionNumber = byteBuffer.getInt();

        byte[] iv;
        AlgorithmParameterSpec parameterSpec;
        if (algorithm == SymmetricAlgorithm.AES_GCM_NOPADDING) {
            iv = new byte[12];
            byteBuffer.get(iv);
            parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        } else if (algorithm == SymmetricAlgorithm.AES_CBC_PKCS5PADDING) {
            iv = new byte[16];
            byteBuffer.get(iv);
            parameterSpec = new IvParameterSpec(iv);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm for decryption: " + algorithm.name());
        }

        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        KeyVersion keyVersion = key.getVersions().stream()
                .filter(v -> v.getVersion() == keyVersionNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Key version " + keyVersionNumber + " not found, cannot decrypt."));

        byte[] decryptedKeyMaterial = rootKeyEncryptor.decrypt(keyVersion.getEncryptedKeyMaterial(), userId);
        SecretKey decryptionKey = new SecretKeySpec(decryptedKeyMaterial, 0, decryptedKeyMaterial.length, "AES");

        Cipher cipher = Cipher.getInstance(algorithm.getJavaName());
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey, parameterSpec);
        return cipher.doFinal(cipherText);
    }

    private KeyVersion getActiveVersion(Key key) {
        int currentVersionNumber = key.getCurrentVersion();
        return key.getVersions().stream()
                .filter(v -> v.getVersion() == currentVersionNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Key data is corrupted. Active version " + currentVersionNumber + " not found for key ID: " + key.getId()
                ));
    }
}