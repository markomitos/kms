package uns.ftn.kms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uns.ftn.kms.model.Key;
import uns.ftn.kms.model.KeyType;
import uns.ftn.kms.model.KeyVersion;
import uns.ftn.kms.repository.KeyRepository;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
@RequiredArgsConstructor
@Service
public class KeyService implements IKeyService {

    private final KeyRepository keyRepository;
    private final AliasGeneratorService aliasGeneratorService;

    private final RootKeyEncryptorService rootKeyEncryptor;
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 256;

    public Key createSymmetricKey() {
        byte[] newKeyMaterial = generateAesKeyMaterial();

        byte[] encryptedMaterial = rootKeyEncryptor.encrypt(newKeyMaterial);

        KeyVersion version1 = new KeyVersion();
        version1.setVersion(1);
        version1.setEncryptedKeyMaterial(encryptedMaterial);
        version1.setCreatedAt(LocalDateTime.now());
        version1.setEnabled(true);


        Key key = new Key();
        version1.setKey(key);
        key.setAlias(aliasGeneratorService.generate());
        key.setType(KeyType.SYMMETRIC_AES);
        key.setCurrentVersion(1);
        key.getVersions().add(version1);

        keyRepository.save(key);
        return key;
    }

    public Key rotateKey(UUID keyId) {
        Key key = keyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Key with ID: " + keyId + " not found."));

        byte[] newKeyMaterial = generateAesKeyMaterial();
        byte[] encryptedMaterial = rootKeyEncryptor.encrypt(newKeyMaterial);

        int newVersionNumber = key.getCurrentVersion() + 1;
        KeyVersion newVersion = new KeyVersion();
        newVersion.setVersion(newVersionNumber);
        newVersion.setEncryptedKeyMaterial(encryptedMaterial);
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setEnabled(true);
        newVersion.setKey(key);

        key.getVersions().add(newVersion);
        key.setCurrentVersion(newVersionNumber);

        keyRepository.save(key);
        return key;
    }

    @Override
    public byte[] getActiveKeyMaterial(UUID keyId) {
        Key key = keyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Key with ID: " + keyId + " not found."));

        int currentVersionNumber = key.getCurrentVersion();
        KeyVersion currentVersion = key.getVersions().get(currentVersionNumber);

        if (currentVersion == null) {
            throw new IllegalStateException("Key data is corrupted. Active version " + currentVersionNumber + " not found for key ID: " + keyId);
        }

        byte[] encryptedMaterial = currentVersion.getEncryptedKeyMaterial();
        return rootKeyEncryptor.decrypt(encryptedMaterial);
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

    @Override
    public Key findKeyById(UUID keyId) {
        return keyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Key with ID: " + keyId + " not found."));
    }
}
