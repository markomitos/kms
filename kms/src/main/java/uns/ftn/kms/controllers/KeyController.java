package uns.ftn.kms.controllers;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.dtos.KeyMaterialResponse;
import uns.ftn.kms.dtos.KeyResponse;
import uns.ftn.kms.models.Key;
import uns.ftn.kms.services.IKeyService;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class KeyController {
    private final IKeyService keyService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<KeyResponse> createKey() {
        Key createdKey = keyService.createSymmetricKey();
        return ResponseEntity.ok(modelMapper.map(createdKey, KeyResponse.class));
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<KeyResponse> rotateKey(@PathVariable UUID keyId) {
        Key rotatedKey = keyService.rotateKey(keyId);
        return ResponseEntity.ok(modelMapper.map(rotatedKey, KeyResponse.class));
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<KeyResponse> getKeyById(@PathVariable UUID keyId) {
        Key key = keyService.findKeyById(keyId);
        return ResponseEntity.ok(modelMapper.map(key, KeyResponse.class));
    }

    @GetMapping("/{keyId}/material")
    public ResponseEntity<KeyMaterialResponse> getActiveKeyMaterial(@PathVariable UUID keyId) {
        byte[] keyMaterialBytes = keyService.getActiveKeyMaterial(keyId);
        String keyMaterialBase64 = Base64.getEncoder().encodeToString(keyMaterialBytes);
        KeyMaterialResponse response = new KeyMaterialResponse(keyId, keyMaterialBase64);
        return ResponseEntity.ok(response);
    }

}


