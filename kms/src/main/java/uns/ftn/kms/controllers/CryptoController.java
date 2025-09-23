package uns.ftn.kms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.annotations.CurrentUser;
import uns.ftn.kms.dtos.AlgorithmResponse;
import uns.ftn.kms.dtos.crypto.requests.AsymmetricDecryptRequest;
import uns.ftn.kms.dtos.crypto.requests.AsymmetricEncryptRequest;
import uns.ftn.kms.dtos.crypto.responses.DecryptResponse;
import uns.ftn.kms.dtos.crypto.responses.EncryptResponse;
import uns.ftn.kms.dtos.envelope.requests.DecryptDataKeyRequest;
import uns.ftn.kms.dtos.envelope.responses.DecryptDataKeyResponse;
import uns.ftn.kms.dtos.envelope.requests.GenerateDataKeyRequest;
import uns.ftn.kms.dtos.envelope.responses.GenerateDataKeyResponse;
import uns.ftn.kms.models.alghoritms.SymmetricAlgorithm;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.services.AlgorithmService;
import uns.ftn.kms.services.CryptoService;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;
    private final AlgorithmService algorithmService;

    @PostMapping("/generate-data-key")
    public ResponseEntity<?> generateDataKey(@RequestBody GenerateDataKeyRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            SymmetricAlgorithm.fromName(request.getAlgorithm());

            GenerateDataKeyResponse response = cryptoService.generateDataKey(
                    request.getAlias(),
                    currentUser.getId(),
                    request.getAlgorithm()
            );
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

    @PostMapping("/encrypt-asymmetric")
    public ResponseEntity<?> encryptAsymmetric(@RequestBody AsymmetricEncryptRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] plaintext = Base64.getDecoder().decode(request.getDataBase64());
            byte[] encryptedData = cryptoService.encryptAsymmetric(request.getAlias(), currentUser.getId(), plaintext, request.getAlgorithm());
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedData);
            return ResponseEntity.ok(new EncryptResponse(encryptedBase64));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/decrypt-asymmetric")
    public ResponseEntity<?> decryptAsymmetric(@RequestBody AsymmetricDecryptRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(request.getDataBase64());
            byte[] decryptedData = cryptoService.decryptAsymmetric(request.getAlias(), currentUser.getId(), encryptedData);
            String decryptedBase64 = Base64.getEncoder().encodeToString(decryptedData);
            return ResponseEntity.ok(new DecryptResponse(decryptedBase64));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Asymmetric decryption failed. You might not be the owner of the key or data is corrupted.");
        }
    }

    @GetMapping("/algorithms/symmetric")
    public ResponseEntity<List<AlgorithmResponse>> getSymmetricAlgorithms() {
        return ResponseEntity.ok(algorithmService.getSymmetricAlgorithms());
    }

    @GetMapping("/algorithms/asymmetric")
    public ResponseEntity<List<AlgorithmResponse>> getAsymmetricAlgorithms() {
        return ResponseEntity.ok(algorithmService.getAsymmetricAlgorithms());
    }
}