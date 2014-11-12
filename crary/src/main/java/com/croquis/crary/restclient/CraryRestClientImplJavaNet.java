package com.croquis.crary.restclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

@TargetApi(14)
public class CraryRestClientImplJavaNet {
	Context mContext;
	Gson mGson;
	String mUserAgent;

	Handler mHandler = new Handler(Looper.getMainLooper());

	public CraryRestClientImplJavaNet(Context context, Gson gson, String userAgent) {
		mContext = context;
		mGson = gson;
		mUserAgent = userAgent;
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}

	public <T> void get(String url, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "GET", null, false, complete, type);
	}

	public <T> void post(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "POST", parameters, false, complete, type);
	}

	public <T> void postGzip(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "POST", parameters, true, complete, type);
	}

	public <T> void put(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "PUT", parameters, false, complete, type);
	}

	public <T> void delete(String url, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "DELETE", null, false, complete, type);
	}

	private <T> void request(final String url, final String method, final Object parameters, final boolean gzip, final CraryRestClient.OnRequestComplete<T> complete, final Type type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection urlConnection;
				try {
					urlConnection = (HttpURLConnection) new URL(url).openConnection();
				} catch (IOException e) {
					if (complete != null) {
						complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
					}
					return;
				} catch (ClassCastException e) {
					if (complete != null) {
						complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
					}
					return;
				}

				try {
					if (mUserAgent != null) {
						urlConnection.setRequestProperty("User-Agent", mUserAgent);
					}
					urlConnection.setRequestMethod(method);
					if (parameters != null) {
						urlConnection.setDoOutput(true);
						urlConnection.setRequestProperty("Content-Type", "application/json");
						if (gzip) {
							urlConnection.setRequestProperty("Content-Encoding", "gzip");
							OutputStream out = urlConnection.getOutputStream();
							out.write(CraryRestClient.gzipDeflate(mGson.toJson(parameters).getBytes()));
							out.close();
						} else {
							OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
							out.write(mGson.toJson(parameters));
							out.close();
						}
					}
				} catch (ProtocolException e) {
					if (complete != null) {
						complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
					}
					urlConnection.disconnect();
					return;
				} catch (IOException e) {
					if (complete != null) {
						complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
					}
					urlConnection.disconnect();
					return;
				}

				processResponse(urlConnection, complete, type);
			}
		}).start();
	}

	private <T> void processResponse(HttpURLConnection urlConnection, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		CraryRestClient.RestError error;
		T json;
		try {
			Reader reader = new InputStreamReader(urlConnection.getInputStream());
			error = getResponseError(urlConnection.getResponseCode(), reader);
			json = (T) mGson.fromJson(reader, type);
		} catch (IOException e) {
			if (complete != null) {
				complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
			}
			urlConnection.disconnect();
			return;
		}
		urlConnection.disconnect();
		if (error != null) {
			callOnComplete(complete, error, null);
		} else {
			callOnComplete(complete, null, json);
		}
	}

	private CraryRestClient.RestError getResponseError(int statusCode, Reader reader) {
		if (statusCode >= 200 && statusCode < 300) {
			return null;
		}
		JsonObject json = mGson.fromJson(reader, JsonObject.class);
		if (json == null) {
			return CraryRestClient.RestError.NETWORK_ERROR;
		}
		JsonElement errorObj = json.get("error");
		String error = errorObj != null && errorObj.isJsonPrimitive() ? errorObj.getAsString() : null;
		JsonElement descriptionObj = json.get("description");
		String description = descriptionObj != null && descriptionObj.isJsonPrimitive() ? descriptionObj.getAsString() : null;
		return new CraryRestClient.RestError(error, description);
	}

	private <T> void callOnComplete(final CraryRestClient.OnRequestComplete<T> complete, final CraryRestClient.RestError error, final T result) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (complete != null) {
					complete.onComplete(error, result);
				}
			}
		});
	}
}
