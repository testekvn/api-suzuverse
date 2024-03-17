package com.suzu.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_ESB_ZERO_TIME = "yyyyMMdd";
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";

    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTime();
    }

    public static String convertDateTimeToString(Date date, String formatDate) {
        SimpleDateFormat format = new SimpleDateFormat(formatDate);
        return format.format(date);
    }

    public static Date subtractMonthFromDate(Date date, Integer month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1 * month);
        return calendar.getTime();
    }

}
