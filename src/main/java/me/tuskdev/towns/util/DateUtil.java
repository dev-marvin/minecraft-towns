package me.tuskdev.towns.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtil {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String now() {
        return DATE_FORMAT.format(System.currentTimeMillis());
    }

}
