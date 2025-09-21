package uns.ftn.kms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uns.ftn.kms.models.Key;

import java.util.UUID;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
}
