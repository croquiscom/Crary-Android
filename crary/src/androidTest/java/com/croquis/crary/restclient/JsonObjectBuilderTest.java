package com.croquis.crary.restclient;

import android.test.AndroidTestCase;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class JsonObjectBuilderTest extends AndroidTestCase {
	public void testBasic() {
		JsonObject json = new JsonObjectBuilder()
				.add("a", 1)
				.add("b", "str")
				.add("c", true)
				.add("d", JsonNull.INSTANCE)
				.build();
		assertEquals(1, json.get("a").getAsInt());
		assertEquals("str", json.get("b").getAsString());
		assertEquals(true, json.get("c").getAsBoolean());
		assertTrue(json.get("d").isJsonNull());
	}
}
