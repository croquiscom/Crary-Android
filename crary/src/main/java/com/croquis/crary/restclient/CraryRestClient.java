package com.croquis.crary.restclient;

import android.content.Context;

import com.croquis.crary.OnTaskComplete;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

public class CraryRestClient {
	public static class RestError extends Throwable {
		private static final long serialVersionUID = 1L;
		public String error;
		public String description;

		public RestError(String error, String description) {
			this.error = error;
			this.description = description;
		}

		public static final RestError NETWORK_ERROR = new RestError("network error", "");
		public static final RestError UNRECOGNIZABLE_RESULT = new RestError("unrecognizable result", "");
		public static final RestError UNKNOWN_ERROR = new RestError("unknown error", "");
	}

	public interface OnRequestComplete<T> extends OnTaskComplete<RestError, T> {
	}

	private static CraryRestClient sSharedClient;

	private CraryRestClientImplApache mImplApache;
	private Gson mGson;
	private String mBaseUrl = "";

	public static CraryRestClient sharedClient(Context context) {
		if (sSharedClient == null) {
			sSharedClient = new CraryRestClient(context);
		}

		return sSharedClient;
	}

	private CraryRestClient(Context context) {
		mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		mImplApache = new CraryRestClientImplApache(context, mGson);
	}

	public void setBaseUrl(String baseUrl) {
		mBaseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return mBaseUrl;
	}

	public void clearSession() {
		mImplApache.clearSession();
	}

	//============================================================
	// Methods for org.json

	public void getUrl(String url, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.getNoCookie(url + convertParametersToQuery(parameters), complete, JSONObject.class);
	}

	public void get(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, JSONObject.class);
	}

	public void getList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
		mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, JSONArray.class);
	}

	public void post(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.post(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void postGzip(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.postGzip(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void post(String path, HttpEntity httpEntity, OnRequestComplete<JSONObject> complete) {
		mImplApache.post(getBaseUrl() + path, httpEntity, complete, JSONObject.class);
	}

	public void postList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
		mImplApache.post(getBaseUrl() + path, parameters, complete, JSONArray.class);
	}

	public void put(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.put(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void put(String path, HttpEntity httpEntity, OnRequestComplete<JSONObject> complete) {
		mImplApache.put(getBaseUrl() + path, httpEntity, complete, JSONObject.class);
	}

	public void delete(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.delete(getBaseUrl() + path + convertParametersToQuery(parameters), complete, JSONObject.class);
	}

	//============================================================
	// Methods for Gson

	public <T> void get(String path, JsonObject parameters, Class<T> klass, OnRequestComplete<T> complete) {
		get(path, parameters, (Type) klass, complete);
	}

	public <T> void get(String path, JsonObject parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
	}

	public <T> void get(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		get(path, parameters, (Type) klass, complete);
	}

	public <T> void get(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
	}

	public <T> void post(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		post(path, parameters, (Type) klass, complete);
	}

	public <T> void post(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.post(getBaseUrl() + path, parameters, complete, type);
	}

	public <T> void postGzip(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		postGzip(path, parameters, (Type) klass, complete);
	}

	public <T> void postGzip(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.postGzip(getBaseUrl() + path, parameters, complete, type);
	}

	public <T> void put(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		put(path, parameters, (Type) klass, complete);
	}

	public <T> void put(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.put(getBaseUrl() + path, parameters, complete, type);
	}

	public <T> void delete(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		delete(path, parameters, (Type) klass, complete);
	}

	public <T> void delete(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		mImplApache.delete(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
	}

	//============================================================
	// Private Methods

	private String convertParametersToQuery(JSONObject parameters) {
		if (parameters == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> jsonIter = parameters.keys();
			while (jsonIter.hasNext()) {
				String key = jsonIter.next();
				sb.append(key).append("=").append(parameters.get(key)).append("&");
			}
		} catch (JSONException e) {
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString().replace(' ', '+');
	}

	private String convertParametersToQuery(JsonObject parameters) {
		if (parameters == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		convertParametersToQuery(sb, "", parameters);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString().replace(' ', '+');
	}

	private void convertParametersToQuery(StringBuilder sb, String path, JsonElement parameters) {
		if (parameters.isJsonObject()) {
			convertParametersToQuery(sb, path, (JsonObject) parameters);
		} else if (parameters.isJsonArray()) {
			convertParametersToQuery(sb, path, (JsonArray) parameters);
		} else if (parameters.isJsonPrimitive()) {
			sb.append(path).append("=").append(parameters.getAsString()).append("&");
		} else {
			// null
			sb.append(path).append("=").append("&");
		}
	}

	private void convertParametersToQuery(StringBuilder sb, String path, JsonObject parameters) {
		for (Map.Entry<String, JsonElement> entry : parameters.entrySet()) {
			String subpath = path.length() > 0 ? path + "[" + entry.getKey() + "]" : entry.getKey();
			convertParametersToQuery(sb, subpath, entry.getValue());
		}
	}

	private void convertParametersToQuery(StringBuilder sb, String path, JsonArray parameters) {
		for (int i = 0; i < parameters.size(); i++) {
			String subpath = path + "[" + i + "]";
			convertParametersToQuery(sb, subpath, parameters.get(i));
		}
	}

	private String convertParametersToQuery(Object parameters) {
		JsonElement json = mGson.toJsonTree(parameters);
		if (json.isJsonObject()) {
			return convertParametersToQuery((JsonObject) json);
		}
		return "";
	}
}
