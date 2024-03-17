package com.suzu.utils;

import org.testng.annotations.Optional;
import org.testng.util.Strings;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

//Final -> We do not want any class to extend this class
public final class DateUtils {

    private DateUtils() {
    }

    /**
     * @return Get Datetime
     */
    public static String getCurrentDate() {
        Date date = new Date();
        return date.toString().replace(":", "_").replace(" ", "_");
    }

    /**
     * @return Format dd/MM/yyyy HH:mm:ss
     */
    public static String getCurrentDateTime(String format) {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
//        System.out.println(formatter.format(now));
//        String Timestamp = now.toString().replace(":", "-");
        return formatter.format(now);
    }

    public static String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    /**
     * @return Unix time
     */
    public static String getCurrentUnixTime() {
        System.currentTimeMillis();
        Date date = new Date();
        return String.valueOf(date.getTime() / 1000);
    }

    /**
     * Convert string to date
     *
     * @param str    : String date
     * @param format : Format
     */
    public static Date convertStrToDate(String str, @Optional String format) {
        if (Strings.isNullOrEmpty(format)) format = "dd/MM/yyyy";
        try {
            return new SimpleDateFormat(format).parse(str.trim());
        } catch (Exception e) {
            Log.error("The string date is invalid.");
        }
        return null;
    }


    /**
     * Subtract the number of specified day
     *
     * @param count : the days to subtract
     */
    public static LocalDate daysAgo(int count) {
        LocalDate date = LocalDate.now();
        return date.minusDays(count);
    }
}
