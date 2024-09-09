package inc.opsnow.xwing.admin.transfer.resource;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResourceUtil {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static String getResource(String resource) {
        StringBuilder json = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResourceAsStream(resource)),
                            StandardCharsets.UTF_8));
            String str;
            while ((str = in.readLine()) != null)
                json.append(str);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return json.toString();
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            // JavaTimeModule 등록
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(Object object, Class<T> cls) {
        try {
            // JavaTimeModule 등록
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.convertValue(object, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

