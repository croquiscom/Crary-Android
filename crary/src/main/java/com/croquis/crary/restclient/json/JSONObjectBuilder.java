package com.croquis.crary.restclient.json;

import com.croquis.crary.util.CraryIso9601DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JSONObjectBuilder {
    private JSONObject mJSONObject;

    public static JSONObject build(String name, boolean value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public static JSONObject build(String name, int value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public static JSONObject build(String name, double value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public static JSONObject build(String name, long value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public static JSONObject build(String name, Date value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public static JSONObject build(String name, Object value) {
        return new JSONObjectBuilder().add(name, value).build();
    }

    public JSONObjectBuilder() {
        mJSONObject = new JSONObject();
    }

    public JSONObjectBuilder add(String name, boolean value) {
        try {
            mJSONObject.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObjectBuilder add(String name, double value) {
        try {
            mJSONObject.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObjectBuilder add(String name, int value) {
        try {
            mJSONObject.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObjectBuilder add(String name, long value) {
        try {
            mJSONObject.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObjectBuilder add(String name, Date value) {
        try {
            if (value != null) {
                mJSONObject.put(name, CraryIso9601DateFormat.format(value));
            } else {
                mJSONObject.put(name, JSONObject.NULL);
            }
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObjectBuilder add(String name, Object value) {
        try {
            mJSONObject.put(name, value != null ? value : JSONObject.NULL);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONObject build() {
        return mJSONObject;
    }
}
