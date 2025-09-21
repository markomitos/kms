package uns.ftn.kms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class KeyVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int version;

    @Column(columnDefinition = "BYTEA")
    private byte[] encryptedKeyMaterial;
    private LocalDateTime createdAt;
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", nullable = false)
    @JsonBackReference
    private Key key;

    public KeyVersion() {
        createdAt = LocalDateTime.now();
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getEncryptedKeyMaterial() {
        return encryptedKeyMaterial;
    }

    public void setEncryptedKeyMaterial(byte[] encryptedKeyMaterial) {
        this.encryptedKeyMaterial = encryptedKeyMaterial;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
