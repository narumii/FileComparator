package uwu.narumi;

import com.twmacinta.util.MD5; //idk how fast this is xd
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileComparator {

    private static final Logger LOGGER = LogManager.getLogger(FileComparator.class);
    private static final Map<String, String> FILES = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Source dir: ");
        Path sourceDir = Paths.get(scanner.nextLine());

        System.out.print("Target dir: ");
        Path targetDir = Paths.get(scanner.nextLine());

        System.out.print("Depth: ");
        int depth = scanner.nextInt();

        if (depth <= 0) {
            LOGGER.error("Depth must be positive");
            return;
        }

        System.out.println();
        LOGGER.info("Source Dir: {}", sourceDir);
        LOGGER.info("Target Dir: {}", targetDir);
        LOGGER.info("Depth: {}\n", depth);

        try {
            Files.walk(sourceDir, depth)
                    .filter(file -> file.toFile().isFile())
                    .forEach(file -> {
                        try {
                            String fileName = file.toString().replace(sourceDir.toString(), "").substring(1);
                            FILES.put(fileName, MD5.asHex(MD5.getHash(file.toFile())));
                        } catch (Exception e) {
                            LOGGER.error("Can't create hash of \"{}\"", file, e);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Can't read \"{}\" directory", sourceDir, e);
        }

        try {
            Files.walk(targetDir, depth)
                    .filter(file -> file.toFile().isFile())
                    .forEach(file -> {
                        try {
                            String fileName = file.toString().replace(targetDir.toString(), "").substring(1);

                            if (!FILES.containsKey(fileName)) {
                                LOGGER.warn("Missing file: \"{}\"", file);
                                return;
                            }

                            String hash = MD5.asHex(MD5.getHash(file.toFile()));
                            if (!FILES.get(fileName).equals(hash)) {
                                LOGGER.warn("Hash mismatch: \"{}\"", fileName);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Can't create hash of: \"{}\"", file, e);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Can't read \"{}\" directory", targetDir, e);
        }

        System.out.println();
        LOGGER.info("Done");
    }
}
