package com.croquis.crary.restclient.gson;

import android.test.AndroidTestCase;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Date;

public class JsonObjectBuilderTest extends AndroidTestCase {
	public void testBasic() {
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
}
