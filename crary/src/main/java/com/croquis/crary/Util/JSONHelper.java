package com.croquis.crary.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONHelper {
	public static String getString(JSONObject json, String name) {
		if (json == null || json.isNull(name)) {
			return null;
		}
		return json.optString(name);
	}

	public static boolean getBoolean(JSONObject json, String name) {
		if (json == null || json.isNull(name)) {
			return false;
		}
		return json.optBoolean(name);
	}

	public static int getInt(JSONObject json, String name) {
		if (json == null || json.isNull(name)) {
			return 0;
		}
		return json.optInt(name);
	}

	public static Integer getInteger(JSONObject json, String name) {
		if (json == null || json.isNull(name)) {
			return null;
		}
		return json.optInt(name);
	}

	public static double getDouble(JSONObject json, String name) {
		if (json == null || json.isNull(name)) {
			return 0;
		}
		return json.optDouble(name);
	}

	public static List<String> getStrings(JSONArray json) {
		if (json == null) {
			return null;
		}
		List<String> array = new ArrayList<String>();
		for (int i = 0; i < json.length(); i++) {
			array.add(json.optString(i, null));
		}
		return array;
	}
}
