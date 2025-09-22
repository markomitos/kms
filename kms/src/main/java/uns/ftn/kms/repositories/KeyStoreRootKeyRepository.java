package uns.ftn.kms.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Optional;
import java.util.UUID;

@Repository("keyStoreRootKeyRepository")
public class KeyStoreRootKeyRepository implements IRootKeyRepository {

    @Value("${keystore.path}")
    private String keyStorePath;

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${keystore.type}")
    private String keyStoreType;

    private KeyStore loadKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        } catch (java.io.FileNotFoundException e) {
            keyStore.load(null, keyStorePassword.toCharArray());
        }
        return keyStore;
    }

    private void saveKeyStore(KeyStore keyStore) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(keyStorePath)) {
            keyStore.store(fos, keyStorePassword.toCharArray());
        }
    }

    @Override
    public synchronized void save(String userId, SecretKey key) {
        try {
            KeyStore keyStore = loadKeyStore();
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
            keyStore.setEntry(userId, secretKeyEntry, new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));
            saveKeyStore(keyStore);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save key for user: " + userId, e);
        }
    }

    @Override
    public synchronized Optional<SecretKey> findByUserId(UUID userId) {
        try {
            KeyStore keyStore = loadKeyStore();
            KeyStore.Entry entry = keyStore.getEntry(String.valueOf(userId), new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));
            if (entry instanceof KeyStore.SecretKeyEntry) {
                return Optional.of(((KeyStore.SecretKeyEntry) entry).getSecretKey());
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find key for user: " + userId, e);
        }
    }

    @Override
    public synchronized void deleteByUserId(String userId) {
        try {
            KeyStore keyStore = loadKeyStore();
            if (keyStore.containsAlias(userId)) {
                keyStore.deleteEntry(userId);
                saveKeyStore(keyStore);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete key for user: " + userId, e);
        }
    }
}