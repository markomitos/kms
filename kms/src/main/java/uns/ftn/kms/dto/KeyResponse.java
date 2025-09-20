package uns.ftn.kms.dto;

import jakarta.persistence.*;
import lombok.Data;
import uns.ftn.kms.model.KeyType;
import uns.ftn.kms.model.KeyVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class KeyResponse {
    private UUID id;
    private String alias;
    private KeyType type;
    private int currentVersion;
    private List<KeyVersionResponse> versions = new ArrayList<>();
}
