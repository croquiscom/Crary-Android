package com.croquis.crary.restclient.json;

import android.test.AndroidTestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class BuilderTest extends AndroidTestCase {
	public void testObjectBuilder() {
		JSONObject json = new JSONObjectBuilder()
				.add("a", 1)
				.add("b", "str")
				.add("c", true)
				.add("d", JSONObject.NULL)
				.add("e", new Date(Date.UTC(114, 10, 25, 14, 10, 15)))
				.build();
		assertEquals(1, json.optInt("a"));
		assertEquals("str", json.optString("b"));
		assertEquals(true, json.optBoolean("c"));
		assertTrue(json.isNull("d"));
		assertEquals("2014-11-25T14:10:15.000Z", json.optString("e"));
	}

	public void testArrayBuilder() {
		JSONArray json = new JSONArrayBuilder()
				.add(1)
				.add("str")
				.add(true)
				.add(JSONObject.NULL)
				.add(new Date(Date.UTC(114, 10, 25, 14, 10, 15)))
				.build();
		assertEquals(1, json.optInt(0));
		assertEquals("str", json.optString(1));
		assertEquals(true, json.optBoolean(2));
		assertTrue(json.isNull(3));
		assertEquals("2014-11-25T14:10:15.000Z", json.optString(4));
	}

	public void testMix() {
		JSONObject json = new JSONObjectBuilder()
				.add("names", new JSONArrayBuilder()
						.add("croquis")
						.add("hello")
						.add("world")
						.build())
				.build();
		assertEquals("{\"names\":[\"croquis\",\"hello\",\"world\"]}", json.toString());
	}
}
