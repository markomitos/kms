package uns.ftn.kms.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uns.ftn.kms.annotations.CurrentUser;
import uns.ftn.kms.dtos.hmac.GenerateHmacRequest;
import uns.ftn.kms.dtos.hmac.GenerateHmacResponse;
import uns.ftn.kms.dtos.hmac.VerifyHmacRequest;
import uns.ftn.kms.dtos.hmac.VerifyHmacResponse;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.services.HmacService;

import java.util.Base64;

@RestController
@RequestMapping("/api/hmac")
@RequiredArgsConstructor
public class HmacController {

    private final HmacService hmacService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody GenerateHmacRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] data = Base64.getDecoder().decode(request.getDataBase64());
            byte[] hmacBytes = hmacService.generate(request.getKeyId(), currentUser.getId(), request.getAlgorithm(), data);
            String hmacBase64 = Base64.getEncoder().encodeToString(hmacBytes);

            GenerateHmacResponse response = new GenerateHmacResponse(request.getKeyId(), request.getAlgorithm(), hmacBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyHmacRequest request, @CurrentUser UserPrincipal currentUser) {
        try {
            byte[] data = Base64.getDecoder().decode(request.getDataBase64());
            byte[] hmacToVerify = Base64.getDecoder().decode(request.getHmacBase64());

            boolean isValid = hmacService.verify(request.getKeyId(), currentUser.getId(), request.getAlgorithm(), data, hmacToVerify);

            return ResponseEntity.ok(new VerifyHmacResponse(isValid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}