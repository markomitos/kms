package uns.ftn.kms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.annotations.CurrentUser;
import uns.ftn.kms.dtos.signing.SignRequest;
import uns.ftn.kms.dtos.signing.SignResponse;
import uns.ftn.kms.dtos.signing.VerifyRequest;
import uns.ftn.kms.dtos.signing.VerifyResponse;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.services.SigningService;

import java.util.Base64;

@RestController
@RequestMapping("/api/signing")
@RequiredArgsConstructor
public class SigningController {

    private final SigningService signingService;

    @PostMapping("/sign")
    public ResponseEntity<?> sign(@RequestBody SignRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] dataToSign = Base64.getDecoder().decode(request.getDataToSignBase64());

            byte[] signatureBytes = signingService.sign(request.getKeyId(), currentUser.getId(), request.getAlgorithm(), dataToSign);

            String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);

            SignResponse response = new SignResponse(request.getKeyId(), request.getAlgorithm(), signatureBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] originalData = Base64.getDecoder().decode(request.getOriginalDataBase64());
            byte[] signature = Base64.getDecoder().decode(request.getSignatureBase64());

            boolean isValid = signingService.verify(request.getKeyId(), currentUser.getId(), request.getAlgorithm(), originalData, signature);

            return ResponseEntity.ok(new VerifyResponse(isValid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}