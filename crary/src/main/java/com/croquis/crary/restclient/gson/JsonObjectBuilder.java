package com.croquis.crary.restclient.gson;

import com.croquis.crary.util.CraryIso9601DateFormat;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Date;

public class JsonObjectBuilder {
    private JsonObject mJsonObject;

    public static JsonObject build(String name, JsonElement value) {
        return new JsonObjectBuilder().add(name, value).build();
    }

    public static JsonObject build(String name, Boolean value) {
        return new JsonObjectBuilder().add(name, value).build();
    }

    public static JsonObject build(String name, Number value) {
        return new JsonObjectBuilder().add(name, value).build();
    }

    public static JsonObject build(String name, String value) {
        return new JsonObjectBuilder().add(name, value).build();
    }

    public static JsonObject build(String name, Date value) {
        return new JsonObjectBuilder().add(name, value).build();
    }

    public JsonObjectBuilder() {
        mJsonObject = new JsonObject();
    }

    public JsonObjectBuilder add(String name, JsonElement value) {
        mJsonObject.add(name, value);
        return this;
    }

    public JsonObjectBuilder add(String name, Boolean value) {
        mJsonObject.addProperty(name, value);
        return this;
    }

    public JsonObjectBuilder add(String name, Number value) {
        mJsonObject.addProperty(name, value);
        return this;
    }

    public JsonObjectBuilder add(String name, String value) {
        mJsonObject.addProperty(name, value);
        return this;
    }

    public JsonObjectBuilder add(String name, Date value) {
        if (value != null) {
            mJsonObject.addProperty(name, CraryIso9601DateFormat.format(value));
        } else {
            mJsonObject.add(name, JsonNull.INSTANCE);
        }
        return this;
    }

    public JsonObject build() {
        return mJsonObject;
    }
}
