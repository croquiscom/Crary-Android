package com.croquis.crary.util;

import android.test.AndroidTestCase;

import java.util.Date;

public class CraryIso9601DateFormatTest extends AndroidTestCase {
    public void testParse() {
        Date date = CraryIso9601DateFormat.parse("2014-11-25T10:30:05.010Z");
        assertEquals(Date.UTC(114, 10, 25, 10, 30, 5) + 10, date.getTime());
    }

    public void testFormat() {
        String string = CraryIso9601DateFormat.format(new Date(Date.UTC(114, 10, 25, 10, 30, 5) + 10));
        assertEquals("2014-11-25T10:30:05.010Z", string);
    }
}
