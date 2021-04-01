package com.example.spmapp.Helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class General {

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }
    public static long getUnixTimeFromDate(Date date) { return date.getTime() / 1000L; }
    public static long getMiddayUnixTime() {
        Calendar calStart = new GregorianCalendar();
        calStart.setTime(new Date());
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        Date midnightYesterday = calStart.getTime();
        return midnightYesterday.getTime()/1000L;
    }
}
