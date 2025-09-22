package uns.ftn.kms.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KeyVersionResponse {
    private int version;
    private LocalDateTime createdAt;
    private boolean enabled;
}
