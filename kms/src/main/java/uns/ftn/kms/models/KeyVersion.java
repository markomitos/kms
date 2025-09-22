package uns.ftn.kms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class KeyVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int version;

    @Column(columnDefinition = "BYTEA")
    private byte[] encryptedKeyMaterial; // Za simetrični ključ ili privatni ključ

    @Column(columnDefinition = "BYTEA")
    private byte[] publicKeyMaterial; // Za javni ključ (biće null za simetrične)

    private LocalDateTime createdAt;
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", nullable = false)
    @JsonBackReference
    private Key key;

    public KeyVersion() {
        createdAt = LocalDateTime.now();
    }
}
