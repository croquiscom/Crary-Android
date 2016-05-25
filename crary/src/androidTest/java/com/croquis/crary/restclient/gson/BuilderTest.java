package com.croquis.crary.restclient.gson;

import android.test.AndroidTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Date;

public class BuilderTest extends AndroidTestCase {
    public void testObjectBuilder() {
        JsonObject json = new JsonObjectBuilder()
                .add("a", 1)
                .add("b", "str")
                .add("c", true)
                .add("d", JsonNull.INSTANCE)
                .add("e", new Date(Date.UTC(114, 10, 25, 14, 10, 15)))
                .build();
        assertEquals(1, json.get("a").getAsInt());
        assertEquals("str", json.get("b").getAsString());
        assertEquals(true, json.get("c").getAsBoolean());
        assertTrue(json.get("d").isJsonNull());
        assertEquals("2014-11-25T14:10:15.000Z", json.get("e").getAsString());
    }

    public void testArrayBuilder() {
        JsonArray json = new JsonArrayBuilder()
                .add(1)
                .add("str")
                .add(true)
                .add(JsonNull.INSTANCE)
                .add(new Date(Date.UTC(114, 10, 25, 14, 10, 15)))
                .build();
        assertEquals(1, json.get(0).getAsInt());
        assertEquals("str", json.get(1).getAsString());
        assertEquals(true, json.get(2).getAsBoolean());
        assertTrue(json.get(3).isJsonNull());
        assertEquals("2014-11-25T14:10:15.000Z", json.get(4).getAsString());
    }

    public void testMix() {
        JsonObject json = new JsonObjectBuilder()
                .add("names", new JsonArrayBuilder()
                        .add("croquis")
                        .add("hello")
                        .add("world")
                        .build())
                .build();
        assertEquals("{\"names\":[\"croquis\",\"hello\",\"world\"]}", json.toString());
    }
}
