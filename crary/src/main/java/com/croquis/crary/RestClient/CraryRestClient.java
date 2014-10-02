package com.croquis.crary.RestClient;

import android.content.Context;

import com.croquis.crary.OnTaskComplete;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONArray;
import org.json.JSONObject;

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
	private String mBaseUrl = "";

	public static CraryRestClient sharedClient(Context context) {
		if (sSharedClient == null) {
			sSharedClient = new CraryRestClient(context);
		}

		return sSharedClient;
	}

	private CraryRestClient(Context context) {
		mImplApache = new CraryRestClientImplApache(context);
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

	public void getUrl(String url, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.getNoCookie(url, parameters, complete, JSONObject.class);
	}

	public void get(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.get(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void getList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
		mImplApache.get(getBaseUrl() + path, parameters, complete, JSONArray.class);
	}

	public void post(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.post(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void postGzip(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.postGzip(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void post(String path, MultipartEntity multipartEntity, OnRequestComplete<JSONObject> complete) {
		mImplApache.post(getBaseUrl() + path, multipartEntity, complete, JSONObject.class);
	}

	public void postList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
		mImplApache.post(getBaseUrl() + path, parameters, complete, JSONArray.class);
	}

	public void put(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.put(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}

	public void put(String path, MultipartEntity multipartEntity, OnRequestComplete<JSONObject> complete) {
		mImplApache.put(getBaseUrl() + path, multipartEntity, complete, JSONObject.class);
	}

	public void delete(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
		mImplApache.delete(getBaseUrl() + path, parameters, complete, JSONObject.class);
	}
}