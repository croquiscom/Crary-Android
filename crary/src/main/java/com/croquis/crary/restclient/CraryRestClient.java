package com.croquis.crary.restclient;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.croquis.crary.OnTaskComplete;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
	private CraryRestClientImplJavaNet mImplJavaNet;
	private Gson mGson;
	private String mBaseUrl = "";

	public static CraryRestClient sharedClient(Context context) {
		if (sSharedClient == null) {
			sSharedClient = new CraryRestClient(context);
		}

		return sSharedClient;
	}

	private CraryRestClient(Context context) {
		String userAgent = null;
		try {
			String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			if (appVersion == null) {
				appVersion = "Unknown";
			}
			int resId = context.getResources().getIdentifier("app_name", "string", context.getPackageName());
			String appName = resId == 0 ? "Unknown" : context.getString(resId);
			userAgent = String.format("%s/%s (%s; Android %s)", appName, appVersion, Build.MODEL, Build.VERSION.RELEASE);
		} catch (PackageManager.NameNotFoundException e) {
		}
		mGson = new GsonBuilder().setFieldNamingStrategy(new GsonFieldNamingStrategy()).create();
		mImplApache = new CraryRestClientImplApache(context, mGson, userAgent);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			mImplJavaNet = new CraryRestClientImplJavaNet(context, mGson, userAgent);
//		}
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
		if (mImplJavaNet != null) {
			mImplJavaNet.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		} else {
			mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		}
	}

	public <T> void get(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		get(path, parameters, (Type) klass, complete);
	}

	public <T> void get(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		if (mImplJavaNet != null) {
			mImplJavaNet.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		} else {
			mImplApache.get(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		}
	}

	public <T> void post(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		post(path, parameters, (Type) klass, complete);
	}

	public <T> void post(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		if (mImplJavaNet != null) {
			mImplJavaNet.post(getBaseUrl() + path, parameters, complete, type);
		} else {
			mImplApache.post(getBaseUrl() + path, parameters, complete, type);
		}
	}

	public <T> void postGzip(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		postGzip(path, parameters, (Type) klass, complete);
	}

	public <T> void postGzip(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		if (mImplJavaNet != null) {
			mImplJavaNet.postGzip(getBaseUrl() + path, parameters, complete, type);
		} else {
			mImplApache.postGzip(getBaseUrl() + path, parameters, complete, type);
		}
	}

	public <T> void put(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		put(path, parameters, (Type) klass, complete);
	}

	public <T> void put(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		if (mImplJavaNet != null) {
			mImplJavaNet.put(getBaseUrl() + path, parameters, complete, type);
		} else {
			mImplApache.put(getBaseUrl() + path, parameters, complete, type);
		}
	}

	public <T> void delete(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
		delete(path, parameters, (Type) klass, complete);
	}

	public <T> void delete(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
		if (mImplJavaNet != null) {
			mImplJavaNet.delete(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		} else {
			mImplApache.delete(getBaseUrl() + path + convertParametersToQuery(parameters), complete, type);
		}
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

	static byte[] gzipDeflate(byte[] data) {
		byte[] gzipped = new byte[0];
		if (data.length != 0) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gzos = new GZIPOutputStream(baos);
				gzos.write(data);
				gzos.close();
				gzipped = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
			}
		}
		return gzipped;
	}

	static byte[] gzipInflate(byte[] data) {
		byte[] ungzipped = new byte[0];
		if (data.length != 0) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(data));
				byte[] buffer = new byte[4096];
				int n;
				while ((n = gzis.read(buffer)) != -1) {
					baos.write(buffer, 0, n);
				}
				gzis.close();
				ungzipped = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
			}
		}
		return ungzipped;
	}

	private static class GsonFieldNamingStrategy implements FieldNamingStrategy {
		@Override
		public String translateName(Field f) {
			String name = f.getName();
			StringBuilder spans = new StringBuilder();
			int i = 0;
			if (name.charAt(0) == 'm' && Character.isUpperCase(name.charAt(1))) {
				// ignore first m that means member
				i = 1;
			}
			for (; i < name.length(); i++) {
				char character = name.charAt(i);
				if (Character.isUpperCase(character) && spans.length() != 0) {
					spans.append("_");
				}
				spans.append(character);
			}
			return spans.toString().toLowerCase();
		}
	}
}
