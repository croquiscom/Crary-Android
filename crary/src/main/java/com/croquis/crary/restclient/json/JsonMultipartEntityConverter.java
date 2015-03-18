package com.croquis.crary.restclient.json;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;

public class JsonMultipartEntityConverter {
	public static MultipartEntity convert(JSONObject object) {
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
		if (object != null) {
			addJSONObject(entity, "", object);
		}
		return entity;
	}

	private static void addPlainObject(MultipartEntity entity, String path, Object object) {
		if (object instanceof JSONObject) {
			addJSONObject(entity, path, (JSONObject) object);
		} else if (object instanceof JSONArray) {
			addJSONArray(entity, path, (JSONArray) object);
		} else if (object != JSONObject.NULL) {
			try {
				entity.addPart(path, new StringBody(object.toString(), Charset.forName("UTF-8")));
			} catch (UnsupportedEncodingException ignored) {
			}
		} else {
			// null
			try {
				entity.addPart(path, new StringBody("", Charset.forName("UTF-8")));
			} catch (UnsupportedEncodingException ignored) {
			}
		}
	}

	private static void addJSONObject(MultipartEntity entity, String path, JSONObject object) {
		//noinspection unchecked
		Iterator<String> i = object.keys();
		while (i.hasNext()) {
			String key = i.next();
			Object value = object.opt(key);
			if (value == null) {
				continue;
			}
			String sub_path = path.length() > 0 ? path + "[" + key + "]" : key;
			addPlainObject(entity, sub_path, value);
		}
	}

	private static void addJSONArray(MultipartEntity entity, String path, JSONArray object) {
		for (int i = 0; i < object.length(); i++) {
			String sub_path = path + "[" + i + "]";
			addPlainObject(entity, sub_path, object.opt(i));
		}
	}
}
