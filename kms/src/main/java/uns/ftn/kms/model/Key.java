package uns.ftn.kms.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Key {
    private UUID id;
    private String alias;
    private KeyType type;
    private int currentVersion;
    private Map<Integer, KeyVersion> versions;

    public Key() {
        versions = new HashMap<>();
        id = UUID.randomUUID();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Map<Integer, KeyVersion> getVersions() {
        return versions;
    }

    public void setVersions(Map<Integer, KeyVersion> versions) {
        this.versions = versions;
    }

    public KeyType getType() {
        return type;
    }

    public void setType(KeyType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
