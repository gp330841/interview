import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

void main() throws Exception {
    Path configPath = Path.of(".specify/config.json");
    if (!Files.exists(configPath)) {
        System.err.println("❌ Error: .specify/config.json does not exist!");
        System.exit(1);
    }
    String configContent = Files.readString(configPath);
    if (!configContent.contains("\"version\"")) {
        System.err.println("❌ Error: invalid config.json format!");
        System.exit(1);
    }
    System.out.println("✓ .specify/config.json validated.");

    List<String> requiredDirs = List.of(
            ".specify/templates",
            ".specify/standards",
            ".specify/workflows",
            ".specify/prompts",
            ".specify/specs"
    );

    for (String dir : requiredDirs) {
        if (!Files.isDirectory(Path.of(dir))) {
            System.err.println("❌ Error: Required SDD directory missing: " + dir);
            System.exit(1);
        }
    }
    System.out.println("✓ All required SDD directories present and verified.");
}
