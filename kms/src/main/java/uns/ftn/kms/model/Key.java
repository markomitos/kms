package uns.ftn.kms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "keys")
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String alias;
    @Enumerated(EnumType.STRING)
    private KeyType type;
    private int currentVersion;

    @OneToMany(mappedBy = "key", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<KeyVersion> versions = new ArrayList<>();

    public List<KeyVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<KeyVersion> versions) {
        this.versions = versions;
    }

    public Key() {
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
