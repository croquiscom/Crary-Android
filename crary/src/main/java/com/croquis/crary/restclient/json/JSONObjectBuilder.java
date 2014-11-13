package com.croquis.crary.restclient.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectBuilder {
	private JSONObject mJSONObject;

	public static JSONObject build(String name, boolean value) {
		return new JSONObjectBuilder().add(name, value).build();
	}

	public static JSONObject build(String name, int value) {
		return new JSONObjectBuilder().add(name, value).build();
	}

	public static JSONObject build(String name, double value) {
		return new JSONObjectBuilder().add(name, value).build();
	}

	public static JSONObject build(String name, long value) {
		return new JSONObjectBuilder().add(name, value).build();
	}

	public static JSONObject build(String name, Object value) {
		return new JSONObjectBuilder().add(name, value).build();
	}

	public JSONObjectBuilder() {
		mJSONObject = new JSONObject();
	}

	public JSONObjectBuilder add(String name, boolean value) {
		try {
			mJSONObject.put(name, value);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONObjectBuilder add(String name, double value) {
		try {
			mJSONObject.put(name, value);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONObjectBuilder add(String name, int value) {
		try {
			mJSONObject.put(name, value);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONObjectBuilder add(String name, long value) {
		try {
			mJSONObject.put(name, value);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONObjectBuilder add(String name, Object value) {
		try {
			mJSONObject.put(name, value != null ? value : JSONObject.NULL);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONObject build() {
		return mJSONObject;
	}
}
