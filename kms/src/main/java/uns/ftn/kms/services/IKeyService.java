package uns.ftn.kms.services;

import uns.ftn.kms.models.Key;

import java.util.UUID;

public interface IKeyService {
    Key createSymmetricKey();
    // Key createAsymmetricKeyPair();
    Key rotateKey(UUID keyId);
    byte[] getActiveKeyMaterial(UUID keyId);

    Key findKeyById(UUID keyId);
}
