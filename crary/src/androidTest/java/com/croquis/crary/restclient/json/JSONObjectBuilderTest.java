package com.croquis.crary.restclient.json;

import android.test.AndroidTestCase;

import org.json.JSONObject;

public class JSONObjectBuilderTest extends AndroidTestCase {
	public void testBasic() {
		JSONObject json = new JSONObjectBuilder()
				.add("a", 1)
				.add("b", "str")
				.add("c", true)
				.add("d", JSONObject.NULL)
				.build();
		assertEquals(1, json.optInt("a"));
		assertEquals("str", json.optString("b"));
		assertEquals(true, json.optBoolean("c"));
		assertTrue(json.isNull("d"));
	}
}
