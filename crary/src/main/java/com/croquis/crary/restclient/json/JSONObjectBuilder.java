package com.croquis.crary.restclient.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JSONObjectBuilder {
	private JSONObject mJSONObject;

	private static DateFormat mFormat;

	static {
		mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		mFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

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

	public static JSONObject build(String name, Date value) {
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

	public JSONObjectBuilder add(String name, Date value) {
		try {
			if (value != null) {
				synchronized (mFormat) {
					mJSONObject.put(name, mFormat.format(value));
				}
			} else {
				mJSONObject.put(name, JSONObject.NULL);
			}
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
