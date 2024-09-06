package inc.opsnow.xwing.admin.common;


import io.quarkus.logging.Log;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class DecryptedConfigSource implements ConfigSource {
    private static final Map<String, String> properties = new HashMap<>();
    private boolean initialized = false;

    @Override
    public Map<String, String> getProperties() {
        if (!initialized) {
            initialize();
        }
        return properties;
    }

    private synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            Log.info("Initializing DecryptedConfigSource");

            loadAESKey().ifPresent(this::setSystemProperties);

            Log.info("DecryptedConfigSource initialized successfully");
        } catch (Exception e) {
            Log.error("Failed to initialize DecryptedConfigSource", e);
        } finally {
            initialized = true;
        }
    }

    private Optional<String> loadAESKey() {
        try {
            String token = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), ".aeskey.properties"))).replace("\n", "");
            return Optional.of(token);
        } catch (IOException e) {
            Log.error("Error loading .aeskey.properties", e);
            return Optional.empty();
        }
    }

    private void setSystemProperties(String aesKey) {
        try {
            String currentProfile = System.getProperty("quarkus.profile", "local");
            Log.info("Current Profile: " + currentProfile);

            List<String> lines = readFileFromClasspath("init-" + currentProfile + ".properties");
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String decryptedValue = decryptFromHex(parts[1], aesKey);

                    switch (parts[0]) {
                        case "DATABASE_URL":
                            properties.put("quarkus.datasource.billnew.reactive.url", decryptedValue);
                            properties.put("quarkus.datasource.jdbc.url", "jdbc:" + decryptedValue);
                            break;
                        case "DATABASE_USERNAME":
                            properties.put("quarkus.datasource.billnew.username", decryptedValue);
                            properties.put("quarkus.datasource.username", decryptedValue);
                            break;
                        case "DATABASE_PASSWORD":
                            properties.put("quarkus.datasource.billnew.password", decryptedValue);
                            properties.put("quarkus.datasource.password", decryptedValue);
                            break;
                    }
                }
            }

            // 로그 출력
//            Log.info("quarkus.datasource.billnew.reactive.url=" + properties.get("quarkus.datasource.billnew.reactive.url"));
//            Log.info("quarkus.datasource.billnew.username=" + properties.get("quarkus.datasource.billnew.username"));
//            Log.info("quarkus.datasource.billnew.password=" + properties.get("quarkus.datasource.billnew.password"));

        } catch (Exception e) {
            Log.error("Error loading init.properties", e);
        }
    }

    // 나머지 메서드들은 그대로 유지...

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public int getOrdinal() {
        return 500;
    }

    @Override
    public String getValue(String key) {
        return getProperties().get(key);
    }

    @Override
    public String getName() {
        return "decryptedConfigSource";
    }


    private List<String> readFileFromClasspath(String fileName) {
        try {
            String data = null;
            try {
                data = new String(Files.readAllBytes(Paths.get(System.getProperty("user.home"), fileName)));
            } catch (Exception e) {

            } finally {
                if (data != null) {
                    return Arrays.stream(data.split("\n")).toList();
                }
            }

            // Get the resource as an InputStream
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + fileName);
            }

            // Read the InputStream into a List of Strings
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // AES-256-ECB 복호화
    private String decryptFromHex(String encryptedData, String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyHash = digest.digest(key.toLowerCase().getBytes(StandardCharsets.UTF_8));

        SecretKeySpec keySpec = new SecretKeySpec(keyHash, "AES");

        // 암호화된 데이터에서 IV 추출
        byte[] iv = HexFormat.of().parseHex(encryptedData.substring(0, 32));
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // IV를 제외한 나머지 부분이 실제 암호화된 데이터
        byte[] encryptedBytes = HexFormat.of().parseHex(encryptedData.substring(32));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}