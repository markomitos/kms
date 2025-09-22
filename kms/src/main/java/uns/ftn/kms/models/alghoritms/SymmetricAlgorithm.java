package uns.ftn.kms.models.alghoritms;

import java.util.Arrays;

public enum SymmetricAlgorithm {
    AES_GCM_NOPADDING((short) 1, "AES/GCM/NoPadding"),
    AES_CBC_PKCS5PADDING((short) 2, "AES/CBC/PKCS5Padding");

    private final short id;
    private final String javaName;

    SymmetricAlgorithm(short id, String javaName) {
        this.id = id;
        this.javaName = javaName;
    }

    public short getId() {
        return id;
    }

    public String getJavaName() {
        return javaName;
    }

    public static SymmetricAlgorithm fromId(short id) {
        return Arrays.stream(values())
                .filter(algo -> algo.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown algorithm ID: " + id));
    }

    public static SymmetricAlgorithm fromName(String name) {
        return Arrays.stream(values())
                .filter(algo -> algo.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown algorithm name: " + name));
    }
}