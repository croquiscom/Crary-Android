package com.croquis.crary.restclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.croquis.crary.restclient.gson.GsonMultipartEntityConverter;
import com.croquis.crary.restclient.json.JsonMultipartEntityConverter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
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

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CraryRestClientImplApache {
    private static final int MAX_TOTAL_CONNECTION = 20;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int TIMEOUT_CONNECT = 15000;
    private static final int TIMEOUT_READ = 15000;

    private static final String COOKIE_SESSION = "cookie_session";
    public static final String SESSION_ID = "connect.sid";

    private static int sAvailableProcessors = Runtime.getRuntime().availableProcessors();
    private static ExecutorService sExecutorService = (sAvailableProcessors > 2) ? Executors.newFixedThreadPool(sAvailableProcessors - 1) : Executors.newFixedThreadPool(1);

    Context mContext;
    HttpClient mClient;
    String mSessionId;
    Gson mGson;

    Handler mHandler = new Handler(Looper.getMainLooper());

    public CraryRestClientImplApache(Context context, Gson gson) {
        mContext = context;
        mGson = gson;
        createClient();
        loadSessionId();
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(mClient.getParams(), userAgent);
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

    private void loadSessionId() {
        mSessionId = getCookieSessionPreference().getString(SESSION_ID, null);
    }

    public boolean clearSession() {
        mSessionId = null;
        return getCookieSessionPreference().edit().clear().commit();
    }

    public String getSessionId() {
        return mSessionId;
    }

    private SharedPreferences getCookieSessionPreference() {
        return mContext.getSharedPreferences(COOKIE_SESSION, Context.MODE_PRIVATE);
    }

    public <T> void getNoCookie(String url, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpGet(url), complete, c, false);
    }

    public <T> void get(String url, OnRequestComplete<T> complete, Type type) {
        request(new HttpGet(url), complete, type, true);
    }

    public <T> void post(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPost(url), parameters != null ? makeHttpEntity(parameters.toString()) : null, complete, c, true);
    }

    public <T> void post(String url, JSONObject parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPost(url), addAttachmentsToMultipartEntity(JsonMultipartEntityConverter.convert(parameters), attachments), complete, c, true);
    }

    public <T> void postGzip(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPost(url), parameters != null ? makeGzipHttpEntity(parameters.toString()) : null, complete, c, true);
    }

    public <T> void post(String url, HttpEntity entity, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPost(url), entity, complete, c, true);
    }

    public <T> void put(String url, JSONObject parameters, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPut(url), parameters != null ? makeHttpEntity(parameters.toString()) : null, complete, c, true);
    }

    public <T> void put(String url, JSONObject parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPut(url), addAttachmentsToMultipartEntity(JsonMultipartEntityConverter.convert(parameters), attachments), complete, c, true);
    }

    public <T> void put(String url, HttpEntity entity, OnRequestComplete<T> complete, Class<T> c) {
        request(new HttpPut(url), entity, complete, c, true);
    }

    public <T> void delete(String url, OnRequestComplete<T> complete, Type type) {
        request(new HttpDelete(url), complete, type, true);
    }

    public <T> void post(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
        request(new HttpPost(url), parameters != null ? makeHttpEntity(mGson.toJson(parameters)) : null, complete, type, true);
    }

    public <T> void post(String url, Object parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<T> complete, Type type) {
        request(new HttpPost(url), addAttachmentsToMultipartEntity(GsonMultipartEntityConverter.convert(parameters, mGson), attachments), complete, type, true);
    }

    public <T> void postGzip(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
        request(new HttpPost(url), parameters != null ? makeGzipHttpEntity(mGson.toJson(parameters)) : null, complete, type, true);
    }

    public <T> void put(String url, Object parameters, OnRequestComplete<T> complete, Type type) {
        request(new HttpPut(url), parameters != null ? makeHttpEntity(mGson.toJson(parameters)) : null, complete, type, true);
    }

    public <T> void put(String url, Object parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<T> complete, Type type) {
        request(new HttpPut(url), addAttachmentsToMultipartEntity(GsonMultipartEntityConverter.convert(parameters, mGson), attachments), complete, type, true);
    }

    public void post(final String url, final byte[] data, final CraryRestClient.OnRequestComplete<byte[]> complete) {
        request(new HttpPost(url), makeBinaryHttpEntity(data), complete, byte[].class, true);
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

        sExecutorService.execute(new Runnable() {
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
        });
    }

    private static HttpEntity makeHttpEntity(String json) {
        StringEntity entity = null;
        if (json != null) {
            try {
                entity = new StringEntity(json, HTTP.UTF_8);
                entity.setContentType("application/json");
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return entity;
    }

    private static HttpEntity makeBinaryHttpEntity(byte[] data) {
        ByteArrayEntity entity = null;
        if (data != null) {
            entity = new ByteArrayEntity(data);
            entity.setContentType("application/octet-stream");
        }
        return entity;
    }

    private static HttpEntity makeGzipHttpEntity(String json) {
        ByteArrayEntity entity = new ByteArrayEntity(CraryRestClient.gzipDeflate(json.getBytes()));
        entity.setContentEncoding("gzip");
        entity.setContentType("application/json");
        return entity;
    }

    private static MultipartEntity addAttachmentsToMultipartEntity(MultipartEntity entity, Collection<CraryRestClientAttachment> attachments) {
        if (attachments != null) {
            for (CraryRestClientAttachment attachment : attachments) {
                entity.addPart(attachment.mName, new ByteArrayBody(attachment.mData, attachment.mMimeType, attachment.mFileName));
            }
        }
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
        if (type == byte[].class) {
            T result = (T) getResponseBinary(response);
            if (result == null) {
                callOnComplete(complete, RestError.NETWORK_ERROR, null);
                return;
            } else {
                callOnComplete(complete, null, result);
                return;
            }
        }

        String result = getResponseString(response);
        if (result == null) {
            callOnComplete(complete, RestError.NETWORK_ERROR, null);
            return;
        }

        RestError error = getResponseError(response, result);
        T json = null;
        if (error == null) {
            if (type == JSONObject.class) {
                try {
                    json = (T) new JSONObject(result);
                } catch (JSONException e) {
                    error = RestError.UNRECOGNIZABLE_RESULT;
                }
            } else if (type == JSONArray.class) {
                try {
                    json = (T) new JSONArray(result);
                } catch (JSONException e) {
                    error = RestError.UNRECOGNIZABLE_RESULT;
                }
            } else {
                try {
                    json = mGson.fromJson(result, type);
                } catch (JsonParseException e) {
                    error = RestError.UNRECOGNIZABLE_RESULT;
                }
            }
        }
        if (error != null) {
            callOnComplete(complete, error, null);
        } else {
            callOnComplete(complete, null, json);
        }
    }

    private RestError getResponseError(HttpResponse response, String result) {
        int statusCode = response != null ? response.getStatusLine().getStatusCode() : -1;
        if (statusCode >= 200 && statusCode < 300) {
            return null;
        }
        JsonObject json;
        try {
            json = mGson.fromJson(new StringReader(result), JsonObject.class);
        } catch (JsonParseException e) {
            return RestError.UNRECOGNIZABLE_RESULT;
        }
        if (json == null) {
            return RestError.NETWORK_ERROR;
        }
        JsonElement errorObj = json.get("error");
        String error = errorObj != null && errorObj.isJsonPrimitive() ? errorObj.getAsString() : null;
        JsonElement descriptionObj = json.get("description");
        String description = descriptionObj != null && descriptionObj.isJsonPrimitive() ? descriptionObj.getAsString() : null;
        return new RestError(statusCode, error, description);
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
                    data = CraryRestClient.gzipInflate(data);
                }
                entity.consumeContent();
                result = new String(data, charset);
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    private byte[] getResponseBinary(HttpResponse response) {
        if (response == null) {
            return null;
        }

        boolean gzipped = false;
        Header encodingHeader = response.getFirstHeader(HTTP.CONTENT_ENCODING);
        if (encodingHeader != null) {
            gzipped = "gzip".equals(encodingHeader.getValue());
        }

        HttpEntity entity = response.getEntity();
        byte[] result = null;
        if (entity != null) {
            try {
                result = EntityUtils.toByteArray(entity);
                if (gzipped) {
                    result = CraryRestClient.gzipInflate(result);
                }
                entity.consumeContent();
            } catch (IOException ignored) {
            }
        }
        return result;
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
