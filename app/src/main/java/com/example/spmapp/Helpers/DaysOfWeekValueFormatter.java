package com.example.spmapp.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DaysOfWeekValueFormatter extends ValueFormatter {


    private ArrayList<String> daysOfWeekList = new ArrayList<>();

    public DaysOfWeekValueFormatter () {
        arrangeXAxisDisplay();
    }

    private void arrangeXAxisDisplay() {
        final String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(new Date());
        Collections.addAll(daysOfWeekList, daysOfWeek);
        int i = daysOfWeekList.indexOf(dayOfWeek);
        daysOfWeekList.add(i, "Today");
        int rotation = daysOfWeekList.size() - (i+1);
        Collections.rotate(daysOfWeekList, rotation);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        try {
            return daysOfWeekList.get((int)value);
        } catch (IndexOutOfBoundsException e) {
            return super.getAxisLabel(value,axis);
        }
    }
}
