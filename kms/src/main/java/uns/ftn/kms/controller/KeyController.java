package uns.ftn.kms.controller;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.dto.CreateKeyRequest;
import uns.ftn.kms.dto.KeyMaterialResponse;
import uns.ftn.kms.model.Key;
import uns.ftn.kms.service.IKeyService;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class KeyController {
    private final IKeyService keyService;

    @PostMapping
    public ResponseEntity<Key> createKey() {
        Key createdKey = keyService.createSymmetricKey();
        return ResponseEntity.ok(createdKey);
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<Key> rotateKey(@PathVariable UUID keyId) {
        Key rotatedKey = keyService.rotateKey(keyId);
        return ResponseEntity.ok(rotatedKey);
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<Key> getKeyById(@PathVariable UUID keyId) {
        Key key = keyService.findKeyById(keyId);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/{keyId}/material")
    public ResponseEntity<KeyMaterialResponse> getActiveKeyMaterial(@PathVariable UUID keyId) {
        byte[] keyMaterialBytes = keyService.getActiveKeyMaterial(keyId);
        String keyMaterialBase64 = Base64.getEncoder().encodeToString(keyMaterialBytes);
        KeyMaterialResponse response = new KeyMaterialResponse(keyId, keyMaterialBase64);
        return ResponseEntity.ok(response);
    }

}


