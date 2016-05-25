package com.croquis.crary.restclient;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.croquis.crary.OnTaskComplete;
import com.croquis.crary.restclient.gson.CraryDateTypeAdapter;
import com.croquis.crary.restclient.gson.CraryFieldNamingStrategy;
import com.croquis.crary.restclient.gson.GsonQueryConverter;
import com.croquis.crary.restclient.json.JsonQueryConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simplifies REST requests
 */
public class CraryRestClient {
    public static class RestError extends Throwable {
        private static final long serialVersionUID = 1L;
        public final int code;
        public final String error;
        public final String description;

        public RestError(int code, String error, String description) {
            this.code = code;
            this.error = error;
            this.description = description;
        }

        public RestError(int code, Throwable cause) {
            super(cause);
            this.code = code;
            this.error = cause.getClass().getName();
            this.description = "";
        }

        public static final RestError NETWORK_ERROR = new RestError(503, "network error", "");
        public static final RestError UNRECOGNIZABLE_RESULT = new RestError(400, "unrecognizable result", "");
        public static final RestError UNKNOWN_ERROR = new RestError(500, "unknown error", "");
    }

    public interface OnRequestComplete<T> extends OnTaskComplete<RestError, T> {
    }

    private static CraryRestClient sSharedClient;

    private final Context mContext;
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
        mContext = context;
        CraryDateTypeAdapter dateTypeAdapter = new CraryDateTypeAdapter();
        mGson = new GsonBuilder()
                .setFieldNamingStrategy(new CraryFieldNamingStrategy())
                .registerTypeAdapter(Date.class, dateTypeAdapter)
                .registerTypeAdapter(Timestamp.class, dateTypeAdapter)
                .registerTypeAdapter(java.sql.Date.class, dateTypeAdapter)
                .create();
        mImplApache = new CraryRestClientImplApache(context, mGson);
        mImplJavaNet = null;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			mImplJavaNet = new CraryRestClientImplJavaNet(context, mGson);
//		}
        setAppName(null);
    }

    /**
     * Sets the base URL that all requests use
     *
     * @param baseUrl a new base URL
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * Returns the base URL
     *
     * @return the current base URL
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void clearSession() {
        if (mImplApache != null) {
            mImplApache.clearSession();
        }
    }

    public String getSessionId() {
        if (mImplApache != null) {
            return mImplApache.getSessionId();
        }
        return null;
    }

    public void setAppName(String appName) {
        String userAgent = null;
        try {
            String appVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            if (appVersion == null) {
                appVersion = "Unknown";
            }
            if (appName == null) {
                int resId = mContext.getResources().getIdentifier("app_name", "string", mContext.getPackageName());
                appName = resId == 0 ? "Unknown" : mContext.getString(resId);
            }
            userAgent = String.format("%s/%s (%s; Android %s)", appName, appVersion, Build.MODEL, Build.VERSION.RELEASE);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        if (mImplApache != null) {
            mImplApache.setUserAgent(userAgent);
        }
        if (mImplJavaNet != null) {
            mImplJavaNet.setUserAgent(userAgent);
        }
    }

    //============================================================
    // Methods for org.json

    private String resolve(String path) {
        try {
            return new URL(new URL(getBaseUrl()), path).toString();
        } catch (MalformedURLException e) {
            return getBaseUrl() + path;
        }
    }

    public void get(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
        mImplApache.get(resolve(path) + JsonQueryConverter.convert(parameters), complete, JSONObject.class);
    }

    public void getList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
        mImplApache.get(resolve(path) + JsonQueryConverter.convert(parameters), complete, JSONArray.class);
    }

    public void post(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
        mImplApache.post(resolve(path), parameters, complete, JSONObject.class);
    }

    public void post(String path, JSONObject parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<JSONObject> complete) {
        mImplApache.post(resolve(path), parameters, attachments, complete, JSONObject.class);
    }

    public void postGzip(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
        mImplApache.postGzip(resolve(path), parameters, complete, JSONObject.class);
    }

    public void post(String path, HttpEntity httpEntity, OnRequestComplete<JSONObject> complete) {
        mImplApache.post(resolve(path), httpEntity, complete, JSONObject.class);
    }

    public void postList(String path, JSONObject parameters, OnRequestComplete<JSONArray> complete) {
        mImplApache.post(resolve(path), parameters, complete, JSONArray.class);
    }

    public void put(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
        mImplApache.put(resolve(path), parameters, complete, JSONObject.class);
    }

    public void put(String path, JSONObject parameters, Collection<CraryRestClientAttachment> attachments, OnRequestComplete<JSONObject> complete) {
        mImplApache.put(resolve(path), parameters, attachments, complete, JSONObject.class);
    }

    public void put(String path, HttpEntity httpEntity, OnRequestComplete<JSONObject> complete) {
        mImplApache.put(resolve(path), httpEntity, complete, JSONObject.class);
    }

    public void delete(String path, JSONObject parameters, OnRequestComplete<JSONObject> complete) {
        mImplApache.delete(resolve(path) + JsonQueryConverter.convert(parameters), complete, JSONObject.class);
    }

    //============================================================
    // Methods for Gson

    public <T> void get(String path, JsonObject parameters, Class<T> klass, OnRequestComplete<T> complete) {
        get(path, parameters, (Type) klass, complete);
    }

    public <T> void get(String path, JsonObject parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.get(resolve(path) + GsonQueryConverter.convert(parameters), complete, type);
        } else {
            mImplApache.get(resolve(path) + GsonQueryConverter.convert(parameters), complete, type);
        }
    }

    public <T> void get(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
        get(path, parameters, (Type) klass, complete);
    }

    public <T> void get(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.get(resolve(path) + GsonQueryConverter.convert(parameters, mGson), complete, type);
        } else {
            mImplApache.get(resolve(path) + GsonQueryConverter.convert(parameters, mGson), complete, type);
        }
    }

    public <T> void post(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
        post(path, parameters, (Type) klass, complete);
    }

    public <T> void post(String path, Object parameters, Collection<CraryRestClientAttachment> attachments, Class<T> klass, OnRequestComplete<T> complete) {
        post(path, parameters, attachments, (Type) klass, complete);
    }

    public <T> void post(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.post(resolve(path), parameters, complete, type);
        } else {
            mImplApache.post(resolve(path), parameters, complete, type);
        }
    }

    public <T> void post(String path, Object parameters, Collection<CraryRestClientAttachment> attachments, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.post(resolve(path), parameters, attachments, complete, type);
        } else {
            mImplApache.post(resolve(path), parameters, attachments, complete, type);
        }
    }

    public <T> void postGzip(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
        postGzip(path, parameters, (Type) klass, complete);
    }

    public <T> void postGzip(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.postGzip(resolve(path), parameters, complete, type);
        } else {
            mImplApache.postGzip(resolve(path), parameters, complete, type);
        }
    }

    public <T> void put(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
        put(path, parameters, (Type) klass, complete);
    }

    public <T> void put(String path, Object parameters, Collection<CraryRestClientAttachment> attachments, Class<T> klass, OnRequestComplete<T> complete) {
        put(path, parameters, attachments, (Type) klass, complete);
    }

    public <T> void put(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.put(resolve(path), parameters, complete, type);
        } else {
            mImplApache.put(resolve(path), parameters, complete, type);
        }
    }

    public <T> void put(String path, Object parameters, Collection<CraryRestClientAttachment> attachments, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.put(resolve(path), parameters, attachments, complete, type);
        } else {
            mImplApache.put(resolve(path), parameters, attachments, complete, type);
        }
    }

    public <T> void delete(String path, Object parameters, Class<T> klass, OnRequestComplete<T> complete) {
        delete(path, parameters, (Type) klass, complete);
    }

    public <T> void delete(String path, Object parameters, Type type, OnRequestComplete<T> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.delete(resolve(path) + GsonQueryConverter.convert(parameters, mGson), complete, type);
        } else {
            mImplApache.delete(resolve(path) + GsonQueryConverter.convert(parameters, mGson), complete, type);
        }
    }

    //============================================================
    // Etc Methods

    public <T> void post(String path, byte[] data, OnRequestComplete<byte[]> complete) {
        if (mImplJavaNet != null) {
            mImplJavaNet.post(resolve(path), data, complete);
        } else {
            mImplApache.post(resolve(path), data, complete);
        }
    }

    //============================================================
    // Private Methods

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
            } catch (IOException ignored) {
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
            } catch (IOException ignored) {
            }
        }
        return ungzipped;
    }
}
