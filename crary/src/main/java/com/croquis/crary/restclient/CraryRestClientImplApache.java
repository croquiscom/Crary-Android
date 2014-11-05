package com.croquis.crary.restclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.croquis.crary.util.JSONHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CraryRestClientImplApache {
	private static final int MAX_TOTAL_CONNECTION = 20;
	private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
	private static final int TIMEOUT_CONNECT = 15000;
	private static final int TIMEOUT_READ = 15000;

	private static final String COOKIE_SESSION = "cookie_session";
	public static final String SESSION_ID = "connect.sid";

	Context mContext;
	HttpClient mClient;
	String mSessionId;
	Gson mGson;

	Handler mHandler = new Handler(Looper.getMainLooper());

	public CraryRestClientImplApache(Context context) {
		mContext = context;
		mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		createClient();
		setUserAgent(mContext, mClient);
		loadSessionId();
	}

	private void createClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

		HttpParams connManagerParams = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(connManagerParams, MAX_TOTAL_CONNECTION);
		ConnManagerParams.setMaxConnectionsPerRoute(connManagerParams, new ConnPerRouteBean(MAX_CONNECTIONS_PER_ROUTE));

		HttpConnectionParams.setConnectionTimeout(connManagerParams, TIMEOUT_CONNECT);
		HttpConnectionParams.setSoTimeout(connManagerParams, TIMEOUT_READ);
		HttpConnectionParams.setTcpNoDelay(connManagerParams, true);

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(connManagerParams, schemeRegistry);
		mClient = new DefaultHttpClient(cm, null);
	}

	private void setUserAgent(Context context, HttpClient client) {
		try {
			String appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			int resId = context.getResources().getIdentifier("app_name", "string", context.getPackageName());
			String appName = resId == 0 ? "" : context.getString(resId);
			String userAgent = String.format("%s/%s (%s; Android %s)", appName, appVersion, Build.MODEL, Build.VERSION.RELEASE);
			HttpProtocolParams.setUserAgent(client.getParams(), userAgent);
		} catch (PackageManager.NameNotFoundException e) {
		}
	}

	private void loadSessionId() {
		mSessionId = getCookieSessionPreference().getString(SESSION_ID, null);
	}

	public boolean clearSession() {
		mSessionId = null;
		return getCookieSessionPreference().edit().clear().commit();
	}

	private SharedPreferences getCookieSessionPreference() {
		return mContext.getSharedPreferences(COOKIE_SESSION, Context.MODE_PRIVATE);
	}

	public <T> void getNoCookie(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpGet(url + convertParametersToQuery(parameters)), complete, c, false);
	}

	public <T> void get(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpGet(url + convertParametersToQuery(parameters)), complete, c, true);
	}

	public <T> void post(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpPost(url), convertParametersToEntity(parameters), complete, c, true);
	}

	public <T> void postGzip(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpPost(url), convertParametersToGzipEntity(parameters), complete, c, true);
	}

	public <T> void post(String url, HttpEntity entity, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpPost(url), entity, complete, c, true);
	}

	public <T> void put(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpPut(url), convertParametersToEntity(parameters), complete, c, true);
	}

	public <T> void put(String url, HttpEntity entity, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpPut(url), entity, complete, c, true);
	}

	public <T> void delete(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
		request(new HttpDelete(url + convertParametersToQuery(parameters)), complete, c, true);
	}

	public <T> void get(String url, JsonObject parameters, OnRequestComplete<T> complete, Type type) {
		request(new HttpGet(url + convertParametersToQuery(parameters)), complete, type, true);
	}

	public <T> void get(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
		request(new HttpGet(url + convertParametersToQuery(parameters)), complete, type, true);
	}

	public <T> void post(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
		request(new HttpPost(url), convertParametersToEntity(parameters), complete, type, true);
	}

	public <T> void postGzip(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
		request(new HttpPost(url), convertParametersToGzipEntity(parameters), complete, type, true);
	}

	private <T> void request(HttpEntityEnclosingRequestBase request, HttpEntity entity,
							 CraryRestClient.OnRequestComplete<T> complete, Type type, boolean useCookie) {
		if (entity != null) {
			request.setEntity(entity);
		}
		request(request, complete, type, useCookie);
	}

	private <T> void request(final HttpRequestBase request, final OnRequestComplete<T> complete, final Type type,
							 final boolean useCookie) {
		if (useCookie) {
			request.setHeader(getCookieHeader());
		}
		request.setHeader("Accept", "application/json");
		request.setHeader("Accept-Encoding", "gzip");

		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpResponse response = null;
				try {
					response = mClient.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (response != null && useCookie) {
					updateCookieSessionID(response);
				}
				processResponse(response, complete, type);

			}
		}).start();
	}

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

	private HttpEntity convertParametersToEntity(JSONObject parameters) {
		StringEntity entity = null;
		if (parameters != null) {
			try {
				entity = new StringEntity(parameters.toString(), HTTP.UTF_8);
				entity.setContentType("application/json");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return entity;
	}

	private HttpEntity convertParametersToEntity(Object parameters) {
		StringEntity entity = null;
		if (parameters != null) {
			try {
				entity = new StringEntity(mGson.toJson(parameters), HTTP.UTF_8);
				entity.setContentType("application/json");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return entity;
	}

	private HttpEntity convertParametersToGzipEntity(JSONObject parameters) {
		ByteArrayEntity entity = new ByteArrayEntity(gzipDeflate(parameters.toString().getBytes()));
		entity.setContentEncoding("gzip");
		entity.setContentType("application/json");
		return entity;
	}

	private HttpEntity convertParametersToGzipEntity(Object parameters) {
		ByteArrayEntity entity = new ByteArrayEntity(gzipDeflate(mGson.toJson(parameters).getBytes()));
		entity.setContentEncoding("gzip");
		entity.setContentType("application/json");
		return entity;
	}

	private Header getCookieHeader() {

		List<Cookie> cookieList = new ArrayList<Cookie>();
		cookieList.add(new BasicClientCookie(SESSION_ID, mSessionId));
		CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
		return cookieSpecBase.formatCookies(cookieList).get(0);
	}

	@SuppressWarnings("unchecked")
	private <T> void processResponse(HttpResponse response, OnRequestComplete<T> complete, Type type) {
		String result = getResponseString(response);
		if (result == null) {
			callOnComplete(complete, RestError.NETWORK_ERROR, null);
			return;
		}

		T json = null;
		if (type == JSONObject.class) {
			try {
				json = (T) new JSONObject(result);
			} catch (JSONException e) {
				callOnComplete(complete, RestError.UNRECOGNIZABLE_RESULT, null);
				return;
			}
		} else if (type == JSONArray.class) {
			try {
				json = (T) new JSONArray(result);
			} catch (JSONException e) {
				callOnComplete(complete, RestError.UNRECOGNIZABLE_RESULT, null);
				return;
			}
		} else {
			json = mGson.fromJson(result, type);
		}
		RestError error = getResponseError(response, json);
		if (error != null) {
			callOnComplete(complete, error, null);
		} else {
			callOnComplete(complete, null, json);
		}
	}

	private String getResponseString(HttpResponse response) {
		if (response == null) {
			return null;
		}

		boolean gzipped = false;
		Header encodingHeader = response.getFirstHeader(HTTP.CONTENT_ENCODING);
		if (encodingHeader != null) {
			gzipped = "gzip".equals(encodingHeader.getValue());
		}

		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			try {
				String charset = EntityUtils.getContentCharSet(entity);
				if (charset == null) {
					charset = HTTP.UTF_8;
				}
				byte[] data = EntityUtils.toByteArray(entity);
				if (gzipped) {
					data = gzipInflate(data);
				}
				entity.consumeContent();
				result = new String(data, charset);
			} catch (IOException e) {
			}
		}
		return result;
	}

	private RestError getResponseError(HttpResponse response, Object json) {
		int statusCode = response != null ? response.getStatusLine().getStatusCode() : -1;
		if (statusCode >= 200 && statusCode < 300) {
			return null;
		}
		if (json == null) {
			return RestError.NETWORK_ERROR;
		}
		if (json instanceof JSONObject) {
			String error = JSONHelper.getString((JSONObject) json, "error");
			String description = JSONHelper.getString((JSONObject) json, "description");
			return new RestError(error, description);
		}
		return RestError.UNKNOWN_ERROR;
	}

	private <T> void callOnComplete(final OnRequestComplete<T> complete, final RestError error, final T result) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (complete != null) {
					complete.onComplete(error, result);
				}
			}
		});
	}

	private byte[] gzipDeflate(byte[] data) {
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

	private byte[] gzipInflate(byte[] data) {
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

	private void updateCookieSessionID(HttpResponse response) {
		String newSessionID = getCookieValue(response, SESSION_ID);
		if (newSessionID != null && !newSessionID.equals(mSessionId)) {
			mSessionId = newSessionID;
			getCookieSessionPreference().edit().putString(SESSION_ID, newSessionID).commit();
		}
	}

	private String getCookieValue(HttpResponse response, String cookieKey) {
		if (response == null) {
			return null;
		}
		Header[] cookieHeaders = response.getHeaders("Set-Cookie");
		if (cookieHeaders == null || cookieHeaders.length == 0) {
			return null;
		}
		String cookieValue = cookieHeaders[0].getValue();
		int startPos = cookieValue.indexOf(cookieKey) + cookieKey.length() + 1;
		int lastPos = cookieValue.indexOf(";", startPos) + 1;
		if (startPos == -1) {
			return null;
		}
		return cookieValue.substring(startPos, lastPos);
	}
}
