package com.croquis.crary.restclient.gson;

import com.croquis.crary.util.CraryIso9601DateFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Date;

public class JsonArrayBuilder {
	private JsonArray mJsonArray;

	public static JsonArray build(JsonElement value) {
		return new JsonArrayBuilder().add(value).build();
	}

	public static JsonArray build(Boolean value) {
		return new JsonArrayBuilder().add(value).build();
	}

	public static JsonArray build(Number value) {
		return new JsonArrayBuilder().add(value).build();
	}

	public static JsonArray build(String value) {
		return new JsonArrayBuilder().add(value).build();
	}

	public static JsonArray build(Date value) {
		return new JsonArrayBuilder().add(value).build();
	}

	public JsonArrayBuilder() {
		mJsonArray = new JsonArray();
	}

	public JsonArrayBuilder add(JsonElement value) {
		mJsonArray.add(value);
		return this;
	}

	public JsonArrayBuilder add(Boolean value) {
		mJsonArray.add(new JsonPrimitive(value));
		return this;
	}

	public JsonArrayBuilder add(Number value) {
		mJsonArray.add(new JsonPrimitive(value));
		return this;
	}

	public JsonArrayBuilder add(String value) {
		mJsonArray.add(new JsonPrimitive(value));
		return this;
	}

	public JsonArrayBuilder add(Date value) {
		if (value != null) {
			mJsonArray.add(new JsonPrimitive(CraryIso9601DateFormat.format(value)));
		} else {
			mJsonArray.add(JsonNull.INSTANCE);
		}
		return this;
	}

	public JsonArray build() {
		return mJsonArray;
	}
}
