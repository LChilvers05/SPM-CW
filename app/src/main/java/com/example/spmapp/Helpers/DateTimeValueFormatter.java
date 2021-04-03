package com.example.spmapp.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date date = new Date((long) (value*1000));
        DateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm", Locale.ENGLISH);
        return dateFormat.format(date);
    }
}
