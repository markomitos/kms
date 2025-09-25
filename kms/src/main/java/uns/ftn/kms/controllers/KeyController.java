package uns.ftn.kms.controllers;

import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.annotations.CurrentUser;
import uns.ftn.kms.dtos.CreateKeyRequest;
import uns.ftn.kms.dtos.KeyMaterialResponse;
import uns.ftn.kms.dtos.KeyResponse;
import uns.ftn.kms.dtos.PublicKeyResponse;
import uns.ftn.kms.models.Key;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.services.IKeyService;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class KeyController {
    private final IKeyService keyService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<KeyResponse> createKey(@RequestBody CreateKeyRequest request, @CurrentUser UserPrincipal currentUser) {
        Key createdKey = keyService.createKey(request, currentUser.getId());
        return ResponseEntity.ok(modelMapper.map(createdKey, KeyResponse.class));
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<KeyResponse> rotateKey(@PathVariable UUID keyId, @CurrentUser UserPrincipal currentUser) {
        Key rotatedKey = keyService.rotateKey(keyId, currentUser.getId());
        return ResponseEntity.ok(modelMapper.map(rotatedKey, KeyResponse.class));
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<KeyResponse> getKeyById(@PathVariable UUID keyId, @CurrentUser UserPrincipal currentUser) {
        Key key = keyService.findKeyById(keyId, currentUser.getId());
        return ResponseEntity.ok(modelMapper.map(key, KeyResponse.class));
    }

    @GetMapping("")
    public ResponseEntity<List<KeyResponse>> getKeysByUser(@CurrentUser UserPrincipal currentUser) {
        Collection<Key> keys = keyService.findKeysByUserId(currentUser.getId());
        List<KeyResponse> keyResponses = keys.stream()
                .map(key -> modelMapper.map(key, KeyResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(keyResponses);
    }

    @GetMapping("/{keyId}/material")
    public ResponseEntity<?> getActiveSymmetricKeyMaterial(@PathVariable UUID keyId, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] keyMaterialBytes = keyService.getActiveSymmetricKeyMaterial(keyId, currentUser.getId());
            String keyMaterialBase64 = Base64.getEncoder().encodeToString(keyMaterialBytes);
            KeyMaterialResponse response = new KeyMaterialResponse(keyId, keyMaterialBase64);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{keyId}/public-key")
    public ResponseEntity<?> getActivePublicKey(@PathVariable UUID keyId, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] publicKeyBytes = keyService.getActivePublicKey(keyId, currentUser.getId());
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);
            PublicKeyResponse response = new PublicKeyResponse(keyId, publicKeyBase64);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}


