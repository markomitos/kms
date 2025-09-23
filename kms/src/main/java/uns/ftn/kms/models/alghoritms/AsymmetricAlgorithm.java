package uns.ftn.kms.models.alghoritms;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AsymmetricAlgorithm {
    RSA_ECB_PKCS1PADDING((short) 101, "RSA/ECB/PKCS1Padding"),
    RSA_ECB_OAEPWithSHA256AndMGF1Padding((short) 102, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

    private final short id;
    private final String javaName;

    AsymmetricAlgorithm(short id, String javaName) {
        this.id = id;
        this.javaName = javaName;
    }

    public static AsymmetricAlgorithm fromId(short id) {
        return Arrays.stream(values())
                .filter(algo -> algo.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown asymmetric algorithm ID: " + id));
    }

    public static AsymmetricAlgorithm fromName(String name) {
        return Arrays.stream(values())
                .filter(algo -> algo.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown asymmetric algorithm name: " + name));
    }
}