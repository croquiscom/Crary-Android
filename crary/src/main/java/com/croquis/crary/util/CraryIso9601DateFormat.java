package com.croquis.crary.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * http://en.wikipedia.org/wiki/ISO_8601
 */
public class CraryIso9601DateFormat {
    private static final DateFormat mFormat;

    static {
        // "'Z'" does not mean a time zone, is just a character Z.
        //noinspection SpellCheckingInspection
        mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String format(Date date) {
        synchronized (mFormat) {
            return mFormat.format(date);
        }
    }

    public static Date parse(String string) {
        synchronized (mFormat) {
            try {
                return mFormat.parse(string);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
