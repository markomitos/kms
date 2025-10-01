package uns.ftn.kms.services;

import uns.ftn.kms.dtos.CreateKeyRequest;
import uns.ftn.kms.models.Key;

import java.util.Collection;
import java.util.UUID;

public interface IKeyService {
    Key createKey(CreateKeyRequest request, UUID userId);
    Key rotateKey(UUID keyId, UUID userId);
    byte[] getActiveSymmetricKeyMaterial(UUID keyId, UUID userId);
    byte[] getActivePublicKey(UUID keyId, UUID userId);
    Key findKeyById(UUID keyId, UUID userId);

    Collection<Key> findKeysByUserId(UUID id);

    byte[] getActivePrivateKeyMaterial(UUID keyId, UUID userId);
}
