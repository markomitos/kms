package uns.ftn.kms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AliasGeneratorService implements IAliasGeneratorService {
    private static final Random RANDOM = new Random();

    private static final List<String> ADJECTIVES = List.of(
            "agile", "ambling", "bold", "bouncing", "brave",
            "chattering", "cheeky", "clever", "climbing", "curious",
            "daring", "energetic", "fast", "fluffy", "foraging",
            "furry", "grasping", "hairy", "howling", "hyper",
            "inquisitive", "intelligent", "jumping", "lanky", "leaping",
            "loud", "mischievous", "nimble", "noisy", "observant",
            "playful", "plucking", "pouncing", "quick", "rambunctious",
            "rowdy", "scampering", "shaggy", "shrieking", "silly",
            "social", "speedy", "squeaking", "swinging", "swift",
            "vocal", "wild", "wise", "young", "zippy"
    );

    private static final List<String> NOUNS = List.of(
            "baboon", "bonobo", "capuchin", "chimpanzee", "colobus",
            "doucs", "drill", "gelada", "gibbon", "gorilla",
            "guenon", "howler", "langur", "leaf", "lemur",
            "macaque", "mandrill", "mangabey", "marmoset", "orangutan",
            "patas", "proboscis", "saki", "siamang", "snubnosed",
            "spider", "squirrel", "tamarin", "tarsier",
            "uakari", "vervet"
    );

    public
    String generate() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return String.format("%s_%s", adjective, noun);
    }
}
