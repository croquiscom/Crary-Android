package com.croquis.crary.restclient.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CraryDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
	private final DateFormat mFormats[];

	public CraryDateTypeAdapter() {
		mFormats = new DateFormat[3];
		// "'Z'" does not mean a time zone, is just a character Z.
		mFormats[0] = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		mFormats[0].setTimeZone(TimeZone.getTimeZone("GMT"));
		mFormats[1] = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS", Locale.US);
		mFormats[1].setTimeZone(TimeZone.getTimeZone("GMT"));
		mFormats[2] = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
		mFormats[2].setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		synchronized (mFormats[0]) {
			return new JsonPrimitive(mFormats[0].format(src));
		}
	}

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (!(json instanceof JsonPrimitive)) {
			throw new JsonParseException("The date should be a string value");
		}
		Date date = parseDate(json.getAsString());
		if (typeOfT == Date.class) {
			return date;
		} else if (typeOfT == Timestamp.class) {
			return new Timestamp(date.getTime());
		} else if (typeOfT == java.sql.Date.class) {
			return new java.sql.Date(date.getTime());
		} else {
			throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
		}
	}

	private Date parseDate(String str) {
		for (DateFormat format : mFormats) {
			synchronized (format) {
				try {
					return format.parse(str);
				} catch (ParseException e) {
				}
			}
		}
		throw new JsonSyntaxException(str);
	}
}
