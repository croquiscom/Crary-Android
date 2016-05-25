package com.croquis.crary.restclient.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

public class GsonMultipartEntityConverter {
    public static MultipartEntity convert(Object object, Gson gson) {
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
        if (object != null) {
            JsonElement json;
            if (object instanceof JsonElement) {
                json = (JsonElement) object;
            } else {
                json = gson.toJsonTree(object);
            }
            addJsonElement(entity, "", json);
        }
        return entity;
    }

    private static void addJsonElement(MultipartEntity entity, String path, JsonElement object) {
        if (object.isJsonObject()) {
            addJsonObject(entity, path, (JsonObject) object);
        } else if (object.isJsonArray()) {
            addJsonArray(entity, path, (JsonArray) object);
        } else if (object.isJsonPrimitive()) {
            try {
                entity.addPart(path, new StringBody(object.getAsString(), Charset.forName("UTF-8")));
            } catch (UnsupportedEncodingException ignored) {
            }
        } else {
            // null
            try {
                entity.addPart(path, new StringBody("", Charset.forName("UTF-8")));
            } catch (UnsupportedEncodingException ignored) {
            }
        }
    }

    private static void addJsonObject(MultipartEntity entity, String path, JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String sub_path = path.length() > 0 ? path + "[" + entry.getKey() + "]" : entry.getKey();
            addJsonElement(entity, sub_path, entry.getValue());
        }
    }

    private static void addJsonArray(MultipartEntity entity, String path, JsonArray object) {
        for (int i = 0; i < object.size(); i++) {
            String sub_path = path + "[" + i + "]";
            addJsonElement(entity, sub_path, object.get(i));
        }
    }
}
