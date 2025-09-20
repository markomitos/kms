package uns.ftn.kms.service;

import uns.ftn.kms.model.Key;

import java.util.UUID;

public interface IKeyService {
    Key createSymmetricKey();
    // Key createAsymmetricKeyPair();
    Key rotateKey(UUID keyId);
    byte[] getActiveKeyMaterial(UUID keyId);

    Key findKeyById(UUID keyId);
}
