package uns.ftn.kms.dtos;

import lombok.Data;
import uns.ftn.kms.models.KeyType;

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