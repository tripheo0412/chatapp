package com.example.triph.chat_sever;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

class Utils {
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
    static boolean isToday(long when) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(when);

        int thenYear = time.get(Calendar.YEAR);
        int thenMonth = time.get(Calendar.MONTH);
        int thenMonthDay = time.get(Calendar.DAY_OF_MONTH);

        time.setTimeInMillis(System.currentTimeMillis());
        return (thenYear == time.get(Calendar.YEAR))
                && (thenMonth == time.get(Calendar.MONTH))
                && (thenMonthDay == time.get(Calendar.DAY_OF_MONTH));
    }

    static boolean isTomorrow(long when) {
        GregorianCalendar time = new GregorianCalendar(TimeZone.getTimeZone("Europe/Helsinki"));
        time.setTimeInMillis(when);

        int thenYear = time.get(Calendar.YEAR);
        int thenMonth = time.get(Calendar.MONTH);
        int thenMonthDay = time.get(Calendar.DAY_OF_MONTH);

        time.setTimeInMillis(System.currentTimeMillis());
        return (thenYear == time.get(Calendar.YEAR))
                && (thenMonth == time.get(Calendar.MONTH))
                && (thenMonthDay == time.get(Calendar.DAY_OF_MONTH) + 1);
    }
}
