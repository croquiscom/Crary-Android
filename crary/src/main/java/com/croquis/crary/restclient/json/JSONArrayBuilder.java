package com.croquis.crary.restclient.json;

import com.croquis.crary.util.CraryIso9601DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JSONArrayBuilder {
	private JSONArray mJSONArray;

	public static JSONArray build(boolean value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public static JSONArray build(int value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public static JSONArray build(double value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public static JSONArray build(long value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public static JSONArray build(Date value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public static JSONArray build(Object value) {
		return new JSONArrayBuilder().add(value).build();
	}

	public JSONArrayBuilder() {
		mJSONArray = new JSONArray();
	}

	public JSONArrayBuilder add(boolean value) {
		mJSONArray.put(value);
		return this;
	}

	public JSONArrayBuilder add(double value) {
		try {
			mJSONArray.put(value);
		} catch (JSONException e) {
		}
		return this;
	}

	public JSONArrayBuilder add(int value) {
		mJSONArray.put(value);
		return this;
	}

	public JSONArrayBuilder add(long value) {
		mJSONArray.put(value);
		return this;
	}

	public JSONArrayBuilder add(Date value) {
		if (value != null) {
			mJSONArray.put(CraryIso9601DateFormat.format(value));
		} else {
			mJSONArray.put(JSONObject.NULL);
		}
		return this;
	}

	public JSONArrayBuilder add(Object value) {
		mJSONArray.put(value != null ? value : JSONObject.NULL);
		return this;
	}

	public JSONArray build() {
		return mJSONArray;
	}
}
