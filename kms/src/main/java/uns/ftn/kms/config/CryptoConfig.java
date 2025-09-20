package uns.ftn.kms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Configuration
public class CryptoConfig {
    @Bean
    public SecretKey rootSecretKey(KmsConfig kmsConfig) {
        try {
            String rootKeyString = kmsConfig.getRootKey();
            if (rootKeyString == null || rootKeyString.isBlank()) {
                throw new IllegalArgumentException("KMS_ROOT_KEY must be set in the environment variables.");
            }

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(rootKeyString.getBytes(StandardCharsets.UTF_8));

            return new SecretKeySpec(keyBytes, "AES");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not create root secret key", e);
        }
    }
}
