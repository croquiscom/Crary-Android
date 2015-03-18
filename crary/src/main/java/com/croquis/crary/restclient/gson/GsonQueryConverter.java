package com.croquis.crary.restclient.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class GsonQueryConverter {
	public static String convert(JsonObject object) {
		if (object == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		addJsonObject(sb, "", object);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString().replace(' ', '+');
	}

	public static String convert(Object object, Gson gson) {
		JsonElement json = gson.toJsonTree(object);
		if (json.isJsonObject()) {
			return convert((JsonObject) json);
		}
		return "";
	}

	private static void addJsonElement(StringBuilder sb, String path, JsonElement object) {
		if (object.isJsonObject()) {
			addJsonObject(sb, path, (JsonObject) object);
		} else if (object.isJsonArray()) {
			addJsonArray(sb, path, (JsonArray) object);
		} else if (object.isJsonPrimitive()) {
			sb.append(path).append("=").append(object.getAsString()).append("&");
		} else {
			// null
			sb.append(path).append("=").append("&");
		}
	}

	private static void addJsonObject(StringBuilder sb, String path, JsonObject object) {
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String sub_path = path.length() > 0 ? path + "[" + entry.getKey() + "]" : entry.getKey();
			addJsonElement(sb, sub_path, entry.getValue());
		}
	}

	private static void addJsonArray(StringBuilder sb, String path, JsonArray object) {
		for (int i = 0; i < object.size(); i++) {
			String sub_path = path + "[" + i + "]";
			addJsonElement(sb, sub_path, object.get(i));
		}
	}
}
