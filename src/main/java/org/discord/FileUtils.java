package org.discord;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUtils {
    public static String readToken() {
        try {
            return Files.readString(Path.of("BOTTOKEN.token"));
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return "";
        }
    }

    public static void saveData(HashMap<String, PlayerData> data) {
        try {
            JsonObject o = new JsonObject();
            for (Map.Entry<String, PlayerData> entry : data.entrySet()) {
                o.addProperty(entry.getKey(), serialObject(entry.getValue()));
            }
            Files.writeString(Path.of("saveData.dat"), o.toString());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static HashMap<String, PlayerData> loadData() {
        try {
            HashMap<String, PlayerData> result = new HashMap<>();

            JsonObject data = JsonParser.parseString(Files.readString(Path.of("saveData.dat"))).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                result.put(entry.getKey(), deSerialObject(entry.getValue().getAsString(), PlayerData.class));
            }
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(System.err);
            return new HashMap<>();
        }
    }

    public static List<String> loadScript() {
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("script.skc");
            if (inputStream == null) return new ArrayList<>();
            var result = Arrays.stream(new String(inputStream.readAllBytes()).split("∮")).map(String::strip).toList();
            inputStream.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deSerialObject(String data, Class<T> ignoredClazz) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
        return (T) ois.readObject();
    }

    public static <T extends Serializable> String serialObject(T obj) throws IOException {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(s);
        oos.writeObject(obj);
        return Base64.getEncoder().encodeToString(s.toByteArray());
    }
}
