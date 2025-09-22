package uns.ftn.kms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uns.ftn.kms.annotations.CurrentUser;
import uns.ftn.kms.dtos.envelope.requests.DecryptDataKeyRequest;
import uns.ftn.kms.dtos.envelope.responses.DecryptDataKeyResponse;
import uns.ftn.kms.dtos.envelope.requests.GenerateDataKeyRequest;
import uns.ftn.kms.dtos.envelope.responses.GenerateDataKeyResponse;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.services.CryptoService;

import java.util.Base64;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @PostMapping("/generate-data-key")
    public ResponseEntity<?> generateDataKey(@RequestBody GenerateDataKeyRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            GenerateDataKeyResponse response = cryptoService.generateDataKey(request.getAlias(), currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/decrypt-data-key")
    public ResponseEntity<?> decryptDataKey(@RequestBody DecryptDataKeyRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] encryptedKey = Base64.getDecoder().decode(request.getEncryptedKeyBase64());
            byte[] plaintextKey = cryptoService.decryptDataKey(request.getAlias(), currentUser.getId(), encryptedKey);
            String plaintextKeyBase64 = Base64.getEncoder().encodeToString(plaintextKey);
            return ResponseEntity.ok(new DecryptDataKeyResponse(plaintextKeyBase64));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Decryption failed. The key alias may be incorrect or the data corrupted.");
        }
    }
}