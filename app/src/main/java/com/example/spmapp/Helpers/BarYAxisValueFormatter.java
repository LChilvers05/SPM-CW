package com.example.spmapp.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class BarYAxisValueFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return Integer.toString((int)(value/3600f));
    }
}
