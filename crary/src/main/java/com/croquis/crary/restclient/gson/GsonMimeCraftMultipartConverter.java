package com.croquis.crary.restclient.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.mimecraft.Multipart;
import com.squareup.mimecraft.Part;

import java.util.Map;

public class GsonMimeCraftMultipartConverter {
	public static Multipart.Builder convert(Object object, Gson gson) {
		Multipart.Builder builder = new Multipart.Builder().type(Multipart.Type.FORM);
		if (object != null) {
			JsonElement json;
			if (object instanceof JsonElement) {
				json = (JsonElement) object;
			} else {
				json = gson.toJsonTree(object);
			}
			addJsonElement(builder, "", json);
		}
		return builder;
	}

	private static void addJsonElement(Multipart.Builder builder, String path, JsonElement object) {
		if (object.isJsonObject()) {
			addJsonObject(builder, path, (JsonObject) object);
		} else if (object.isJsonArray()) {
			addJsonArray(builder, path, (JsonArray) object);
		} else if (object.isJsonPrimitive()) {
			builder.addPart(new Part.Builder()
					.body(object.getAsString())
					.contentDisposition("form-data; name=\"" + path + "\"")
					.build());
		} else {
			// null
			builder.addPart(new Part.Builder()
					.body("")
					.contentDisposition("form-data; name=\"" + path + "\"")
					.build());
		}
	}

	private static void addJsonObject(Multipart.Builder builder, String path, JsonObject object) {
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String sub_path = path.length() > 0 ? path + "[" + entry.getKey() + "]" : entry.getKey();
			addJsonElement(builder, sub_path, entry.getValue());
		}
	}

	private static void addJsonArray(Multipart.Builder builder, String path, JsonArray object) {
		for (int i = 0; i < object.size(); i++) {
			String sub_path = path + "[" + i + "]";
			addJsonElement(builder, sub_path, object.get(i));
		}
	}
}
