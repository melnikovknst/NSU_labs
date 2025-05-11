package chat.server;

import chat.exceptions.DefaultConfigMissingException;
import chat.exceptions.UserConfigNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Scanner;

public class ServerApp {
    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/config.json";

    public static void main(String[] args) {
        try {
            Map<String, Object> config;

            if (args.length > 0) {
                config = tryLoadUserConfig(args[0]);
            } else {
                config = loadDefaultConfig();
            }

            int port = (int) config.get("port");
            boolean logging = (boolean) config.get("logging");
            String protocol = (String) config.get("protocol");

            System.out.println("[MODE: " + protocol.toUpperCase() + "] Server starting on port " + port);

            if ("json".equalsIgnoreCase(protocol)) {
                new ServerCoreJson(port, logging).start();
            } else if ("object".equalsIgnoreCase(protocol)) {
                new ServerCoreObject(port, logging).start();
            } else {
                System.out.println("Unknown protocol: " + protocol);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Map<String, Object> tryLoadUserConfig(String path) throws IOException {
        try {
            return readConfig(path);
        } catch (UserConfigNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.print("Use default config? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                return loadDefaultConfig();
            } else {
                System.out.println("Exiting.");
                System.exit(0);
            }
        }
        return null;
    }

    private static Map<String, Object> loadDefaultConfig() throws IOException {
        return readConfig(DEFAULT_CONFIG_PATH);
    }

    private static Map<String, Object> readConfig(String path) throws IOException {
        Path configPath = Paths.get(path);
        if (!Files.exists(configPath)) {
            if (path.equals(DEFAULT_CONFIG_PATH)) {
                throw new DefaultConfigMissingException();
            } else {
                throw new UserConfigNotFoundException();
            }
        }
        byte[] data = Files.readAllBytes(configPath);
        return new ObjectMapper().readValue(data, Map.class);
    }
}
