package uns.ftn.kms.repositories;

import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

public interface IRootKeyRepository {
    void save(String userId, SecretKey key);
    Optional<SecretKey> findByUserId(UUID userId);
    void deleteByUserId(String userId);
}