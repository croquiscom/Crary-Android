package com.croquis.crary.restclient.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonQueryConverter {
	public static String convert(JSONObject object) {
		if (object == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		addJSONObject(sb, "", object);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString().replace(' ', '+');
	}

	private static void addPlainObject(StringBuilder sb, String path, Object object) {
		if (object instanceof JSONObject) {
			addJSONObject(sb, path, (JSONObject) object);
		} else if (object instanceof JSONArray) {
			addJSONArray(sb, path, (JSONArray) object);
		} else if (object != JSONObject.NULL) {
			sb.append(path).append("=").append(object.toString()).append("&");
		} else {
			// null
			sb.append(path).append("=").append("&");
		}
	}

	private static void addJSONObject(StringBuilder sb, String path, JSONObject object) {
		Iterator<String> i = object.keys();
		while (i.hasNext()) {
			String key = i.next();
			Object value = object.opt(key);
			if (value == null) {
				continue;
			}
			String subpath = path.length() > 0 ? path + "[" + key + "]" : key;
			addPlainObject(sb, subpath, value);
		}
	}

	private static void addJSONArray(StringBuilder sb, String path, JSONArray object) {
		for (int i = 0; i < object.length(); i++) {
			String subpath = path + "[" + i + "]";
			addPlainObject(sb, subpath, object.opt(i));
		}
	}
}
