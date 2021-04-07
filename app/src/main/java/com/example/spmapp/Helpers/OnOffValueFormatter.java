package com.example.spmapp.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class OnOffValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (value == 0.5f) {
            return "On";
        } else if (value == 0f) {
            return "Off";
        }
        return "";
    }
}
