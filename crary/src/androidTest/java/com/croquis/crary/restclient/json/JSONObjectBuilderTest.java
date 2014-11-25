package com.croquis.crary.restclient.json;

import android.test.AndroidTestCase;

import org.json.JSONObject;

import java.util.Date;

public class JSONObjectBuilderTest extends AndroidTestCase {
	public void testBasic() {
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
}
