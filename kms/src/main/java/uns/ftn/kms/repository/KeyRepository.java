package uns.ftn.kms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uns.ftn.kms.model.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class KeyRepository {
    private final String storagePath = "keys-storage/";
    private final ObjectMapper objectMapper;
    private final Path storageDirectory;

    public KeyRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.storageDirectory = Paths.get(storagePath);
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory: " + storagePath, e);
        }
    }
    public void save(Key key) {
        try {
            Path filePath = storageDirectory.resolve(key.getId().toString() + ".json");
            String jsonString = objectMapper.writeValueAsString(key);
            Files.writeString(filePath, jsonString);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save key with id: " + key.getId(), e);
        }
    }

    public Optional<Key> findById(UUID id) {
        try {
            Path filePath = storageDirectory.resolve(id.toString() + ".json");

            if (!Files.exists(filePath)) {
                return Optional.empty();
            }

            String jsonString = Files.readString(filePath);
            Key key = objectMapper.readValue(jsonString, Key.class);
            return Optional.of(key);

        } catch (IOException e) {
            throw new RuntimeException("Failed to find or read key with id: " + id, e);
        }
    }
}
