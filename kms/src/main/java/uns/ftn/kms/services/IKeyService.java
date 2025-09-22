package uns.ftn.kms.services;

import uns.ftn.kms.models.Key;

import java.util.UUID;

public interface IKeyService {
    Key createSymmetricKey(UUID userId);
    // Key createAsymmetricKeyPair();
    Key rotateKey(UUID keyId, UUID userId);
    byte[] getActiveKeyMaterial(UUID keyId, UUID userId);

    Key findKeyById(UUID keyId);
}
