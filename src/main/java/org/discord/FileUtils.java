package org.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {
    public static void saveData(HashMap<String, Data> data) {
        try {
            JsonObject o = new JsonObject();
            for (Map.Entry<String, Data> entry : data.entrySet()) {
                o.addProperty(entry.getKey(), serialObject(entry.getValue()));
            }
            Files.writeString(Path.of("saveData.dat"), o.toString());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    public static HashMap<String, Data> loadData() {
        try {
            HashMap<String, Data> result = new HashMap<>();
            JsonObject data = new JsonParser().parse(Files.readString(Path.of("saveData.dat"))).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                result.put(entry.getKey(), deSerialObject(entry.getValue().getAsString(), Data.class));
            }
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(System.err);
            return new HashMap<>();
        }
    }

    public static <T extends Serializable> T deSerialObject(String data, Class<T> clazz) throws IOException, ClassNotFoundException {
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
