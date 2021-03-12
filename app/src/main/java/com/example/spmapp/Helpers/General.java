package com.example.spmapp.Helpers;

import java.util.Date;

public class General {

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }
    public static long getUnixTimeFromDate(Date date) { return date.getTime() / 1000L; }
}
