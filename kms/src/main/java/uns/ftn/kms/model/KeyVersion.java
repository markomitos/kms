package uns.ftn.kms.model;

import java.time.LocalDateTime;

public class KeyVersion {
    private int version;
    private byte[] encryptedKeyMaterial;
    private LocalDateTime createdAt;
    private boolean enabled;

    public KeyVersion() {
        createdAt = LocalDateTime.now();
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
