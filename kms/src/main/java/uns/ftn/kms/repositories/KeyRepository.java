package uns.ftn.kms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uns.ftn.kms.models.Key;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
    Optional<Key> findByIdAndUserId(UUID id, UUID userId);
    Optional<Key> findByAliasAndUserId(String alias, UUID userId);
    Optional<Key> findByAlias(String alias);
}
