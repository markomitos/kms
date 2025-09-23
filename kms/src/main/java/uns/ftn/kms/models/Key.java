package uns.ftn.kms.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "keys")
@Data
public class Key {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String alias;
    @Enumerated(EnumType.STRING)
    private KeyType type;
    @Column(nullable = false)
    private int currentVersion;

    @Column(nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "key", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<KeyVersion> versions = new ArrayList<>();

    public Key() {
    }
}
