package com.example.spmapp;

import android.content.Context;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartFactory {

    Context context;

    public ChartFactory(Context context) {
        this.context = context;
    }

    public BarDataSet createBarDataSet(ArrayList<Double> values, String title) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            BarEntry barEntry = new BarEntry(i, values.get(i).floatValue());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        return barDataSet;
    }

    public BarChart createBarChart(List<IBarDataSet> dataSets) {
        BarData data = new BarData(dataSets);
        BarChart barChart = new BarChart(context);

        XAxis xAxis = barChart.getXAxis();
        //TODO: AxisValueFormatter

        float groupSpace = 0.08f;
        float barSpace = 0.02f;
        float barWidth = 0.4f;

        barChart.setData(data);
        barChart.getBarData().setBarWidth(barWidth);
//        barChart.getXAxis().setAxisMinimum(//group);
//        barChart.groupBars(//group, groupSpace, barSpace);

        return barChart;
    }
}