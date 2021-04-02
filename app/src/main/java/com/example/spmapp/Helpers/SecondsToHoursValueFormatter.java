package com.example.spmapp.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class SecondsToHoursValueFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return (int) (value / 3600f) + "hrs";
    }

    @Override
    public String getFormattedValue(float value) {
        return Integer.toString((int)(value / 3600f));
    }
}
