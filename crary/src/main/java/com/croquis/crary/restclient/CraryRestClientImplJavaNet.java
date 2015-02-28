package com.croquis.crary.restclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.croquis.crary.restclient.gson.GsonMimeCraftMultipartConverter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.squareup.mimecraft.Multipart;
import com.squareup.mimecraft.Part;

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
import java.util.Collection;
import java.util.Map;

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
		request(url, "GET", null, null, false, complete, type);
	}

	public <T> void post(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "POST", parameters, null, false, complete, type);
	}

	public <T> void post(String url, Object parameters, Collection<CraryRestClientAttachment> attachments, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "POST", parameters, attachments, false, complete, type);
	}

	public <T> void postGzip(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "POST", parameters, null, true, complete, type);
	}

	public <T> void put(String url, Object parameters, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "PUT", parameters, null, false, complete, type);
	}

	public <T> void put(String url, Object parameters, Collection<CraryRestClientAttachment> attachments, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "PUT", parameters, attachments, false, complete, type);
	}

	public <T> void delete(String url, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		request(url, "DELETE", null, null, false, complete, type);
	}

	private <T> void request(final String url, final String method, final Object parameters, final Collection<CraryRestClientAttachment> attachments, final boolean gzip, final CraryRestClient.OnRequestComplete<T> complete, final Type type) {
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
					if (attachments != null) {
						urlConnection.setDoOutput(true);
						Multipart multipart = convertMultipart(parameters, attachments);
						Map<String, String> headers = multipart.getHeaders();
						for (String field : headers.keySet()) {
							urlConnection.setRequestProperty(field, headers.get(field));
						}
						multipart.writeBodyTo(urlConnection.getOutputStream());
					} else if (parameters != null) {
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

	private Multipart convertMultipart(Object parameters, Collection<CraryRestClientAttachment> attachments) {
		Multipart.Builder builder = GsonMimeCraftMultipartConverter.convert(parameters, mGson);
		for (CraryRestClientAttachment attachment : attachments) {
			builder.addPart(new Part.Builder()
					.body(attachment.mData)
					.contentType(attachment.mMimeType)
					.contentDisposition("form-data; name=\"" + attachment.mName + "\"; filename=\"" + attachment.mFileName + "\"")
					.build());
		}
		return builder.build();
	}

	private <T> void processResponse(HttpURLConnection urlConnection, CraryRestClient.OnRequestComplete<T> complete, Type type) {
		CraryRestClient.RestError error;
		T json;
		try {
			Reader reader = new InputStreamReader(urlConnection.getInputStream());
			error = getResponseError(urlConnection.getResponseCode(), reader);
			//noinspection unchecked
			json = mGson.fromJson(reader, type);
		} catch (IOException e) {
			if (complete != null) {
				complete.onComplete(CraryRestClient.RestError.UNKNOWN_ERROR, null);
			}
			urlConnection.disconnect();
			return;
		} catch (JsonParseException e) {
			if (complete != null) {
				complete.onComplete(new CraryRestClient.RestError(400, e), null);
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
		return new CraryRestClient.RestError(statusCode, error, description);
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
