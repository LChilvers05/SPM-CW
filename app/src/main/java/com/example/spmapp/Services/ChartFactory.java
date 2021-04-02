package com.example.spmapp.Services;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.example.spmapp.Helpers.DaysOfWeekValueFormatter;
import com.example.spmapp.Helpers.SecondsToHoursValueFormatter;
import com.example.spmapp.Models.ChartSession;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartFactory {
    //creating bar chart
    Context context;

    public ChartFactory(Context context) {
        this.context = context;
    }

    //BAR CHART
    private void configureBarChartAppearance(BarChart chart) {
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(8f + 0.25f);
        xAxis.setValueFormatter(new DaysOfWeekValueFormatter());

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(12*3600f);
        leftAxis.setValueFormatter(new SecondsToHoursValueFormatter());
        leftAxis.setSpaceTop(35f);

        chart.getAxisRight().setEnabled(false);

        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public BarDataSet createBarDataSet(ArrayList<ChartSession> bars, String title) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < bars.size(); i++) {
            BarEntry barEntry = new BarEntry(i, bars.get(i).getDuration());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);

        if (title.equals("Sleep")) {
            barDataSet.setColor(Color.parseColor("#2B6481"));
        } else if (title.equals("Downtime")) {
            barDataSet.setColor(Color.parseColor("#EDAA3E"));
        } else {
            barDataSet.setColor(Color.parseColor("#D83775"));
        }

        barDataSet.setValueFormatter(new SecondsToHoursValueFormatter());
        return barDataSet;
    }

    public BarChart createBarChart(List<IBarDataSet> dataSets) {
        BarData data = new BarData(dataSets);
        BarChart barChart = new BarChart(context);
        configureBarChartAppearance(barChart);

        float barSpace = 0.1f;
        float barWidth = 0.2f;
        float groupSpace = 1.0f - ((barSpace + barWidth) * dataSets.size());

        barChart.setData(data);
        barChart.getBarData().setBarWidth(barWidth);
        barChart.groupBars(0, groupSpace, barSpace);

        return barChart;
    }

    //LINE CHART

    private void configureLineChartAppearance(LineChart chart) {
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1f);
        leftAxis.setLabelCount(1);
        leftAxis.setSpaceTop(35f);

        chart.getAxisRight().setEnabled(false);

        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public LineDataSet createLineDataSet(ArrayList<ChartSession> sessions) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (ChartSession session : sessions) {
            //starting point
            entries.add(new Entry(session.getStart(), 0f));
            //start of screen time
            entries.add(new Entry(session.getStart(), 0.5f));
            //end of screen time
            entries.add(new Entry(session.getEnd(), 0.5f));
            //ending point
            entries.add(new Entry(session.getEnd(), 0f));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Screen Usage");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(2f);

        return dataSet;
    }

    public LineChart createLineChart(LineDataSet dataSet) {
        LineData data = new LineData(dataSet);
        LineChart lineChart = new LineChart(context);
        configureLineChartAppearance(lineChart);

        lineChart.setData(data);

        return lineChart;
    }
}
