package uns.ftn.kms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.annotations.KmsAuditLog;
import uns.ftn.kms.dtos.CreateKeyRequest;
import uns.ftn.kms.exceptions.EntityNotFoundException;
import uns.ftn.kms.models.Key;
import uns.ftn.kms.models.KeyType;
import uns.ftn.kms.models.KeyVersion;
import uns.ftn.kms.repositories.KeyRepository;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class KeyService implements IKeyService {

    private final KeyRepository keyRepository;
    private final AliasGeneratorService aliasGeneratorService;

    private final RootKeyEncryptorService rootKeyEncryptor;
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 256;

    @KmsAuditLog(action = "CREATE_KEY")
    public Key createKey(CreateKeyRequest request, UUID userId) {
        switch (request.getKeyType()) {
            case SYMMETRIC_AES:
                return createSymmetricKey(userId);
            case ASYMMETRIC_RSA:
                return createAsymmetricKey(userId, request.getKeySize());
            default:
                throw new IllegalArgumentException("Unsupported key type: " + request.getKeyType());
        }
    }

    private Key createSymmetricKey(UUID userId) {
        byte[] newKeyMaterial = generateAesKeyMaterial();

        byte[] encryptedMaterial = rootKeyEncryptor.encrypt(newKeyMaterial, userId);

        KeyVersion version1 = new KeyVersion();
        version1.setVersion(1);
        version1.setEncryptedKeyMaterial(encryptedMaterial);
        version1.setCreatedAt(LocalDateTime.now());
        version1.setEnabled(true);


        Key key = new Key();
        version1.setKey(key);
        key.setAlias(aliasGeneratorService.generate());
        key.setUserId(userId);
        key.setType(KeyType.SYMMETRIC_AES);
        key.setCurrentVersion(1);
        key.getVersions().add(version1);

        keyRepository.save(key);
        return key;
    }

    private Key createAsymmetricKey(UUID userId, int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize > 0 ? keySize : 2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            byte[] privateKeyMaterial = keyPair.getPrivate().getEncoded();
            byte[] publicKeyMaterial = keyPair.getPublic().getEncoded();

            byte[] encryptedPrivateKey = rootKeyEncryptor.encrypt(privateKeyMaterial, userId);

            KeyVersion version1 = new KeyVersion();
            version1.setVersion(1);
            version1.setEncryptedKeyMaterial(encryptedPrivateKey);
            version1.setPublicKeyMaterial(publicKeyMaterial);

            Key key = new Key();
            version1.setKey(key);
            key.setAlias(aliasGeneratorService.generate());
            key.setUserId(userId);
            key.setCurrentVersion(1);
            key.setType(KeyType.ASYMMETRIC_RSA);
            key.getVersions().add(version1);

            return keyRepository.save(key);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create asymmetric key", e);
        }
    }

    @KmsAuditLog(action = "ROTATE_KEY")
    public Key rotateKey(UUID keyId, UUID userId) {
        Key key = findKeyById(keyId, userId);

        switch (key.getType()) {
            case SYMMETRIC_AES:
                byte[] newAesMaterial = generateAesKeyMaterial();
                byte[] encryptedAesMaterial = rootKeyEncryptor.encrypt(newAesMaterial, userId);

                KeyVersion newAesVersion = createNewVersion(key, encryptedAesMaterial, null);
                key.getVersions().add(newAesVersion);
                key.setCurrentVersion(newAesVersion.getVersion());
                break;

            case ASYMMETRIC_RSA:
                KeyPair newRsaPair = generateRsaKeyPair(2048);
                byte[] privateKeyMaterial = newRsaPair.getPrivate().getEncoded();
                byte[] publicKeyMaterial = newRsaPair.getPublic().getEncoded();
                byte[] encryptedPrivateKey = rootKeyEncryptor.encrypt(privateKeyMaterial, userId);

                KeyVersion newRsaVersion = createNewVersion(key, encryptedPrivateKey, publicKeyMaterial);
                key.getVersions().add(newRsaVersion);
                key.setCurrentVersion(newRsaVersion.getVersion());
                break;

            default:
                throw new IllegalStateException("Rotation not supported for key type: " + key.getType());
        }

        return keyRepository.save(key);
    }

    private KeyVersion createNewVersion(Key key, byte[] encryptedMaterial, byte[] publicMaterial) {
        int newVersionNumber = key.getCurrentVersion() + 1;
        KeyVersion newVersion = new KeyVersion();
        newVersion.setVersion(newVersionNumber);
        newVersion.setEncryptedKeyMaterial(encryptedMaterial);
        newVersion.setPublicKeyMaterial(publicMaterial);
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setEnabled(true);
        newVersion.setKey(key);
        return newVersion;
    }

    private KeyPair generateRsaKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize > 0 ? keySize : 2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA algorithm not available", e);
        }
    }

    @KmsAuditLog(action = "GET_ACTIVE_SYMMETRIC_KEY_MATERIAL")
    public byte[] getActiveSymmetricKeyMaterial(UUID keyId, UUID userId) {
        Key key = findKeyById(keyId, userId);
        if (key.getType() != KeyType.SYMMETRIC_AES) {
            throw new IllegalArgumentException("Key is not a symmetric key.");
        }
        KeyVersion currentVersion = getActiveVersion(key);

        return rootKeyEncryptor.decrypt(currentVersion.getEncryptedKeyMaterial(), userId);
    }

    @KmsAuditLog(action = "GET_ACTIVE_PUBLIC_KEY")
    public byte[] getActivePublicKey(UUID keyId, UUID userId) {
        Key key = findKeyById(keyId, userId);
        if (key.getType() != KeyType.ASYMMETRIC_RSA) {
            throw new IllegalArgumentException("Key is not an asymmetric key.");
        }
        KeyVersion currentVersion = getActiveVersion(key);

        if (currentVersion.getPublicKeyMaterial() == null) {
            throw new IllegalStateException("Public key material not found for this key version.");
        }
        return currentVersion.getPublicKeyMaterial();
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

    private byte[] generateAesKeyMaterial() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES algorithm not available", e);
        }
    }

    @KmsAuditLog(action = "GET_KEY_METADATA")
    @Override
    public Key findKeyById(UUID keyId, UUID userId) {
        return keyRepository.findByIdAndUserId(keyId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Key not found or you do not have permission."));
    }

    @Override
    public Collection<Key> findKeysByUserId(UUID id) {
        return keyRepository.findAllByUserId(id);
    }
}
