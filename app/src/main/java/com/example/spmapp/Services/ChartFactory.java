package com.example.spmapp.Services;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.example.spmapp.Helpers.BarYAxisValueFormatter;
import com.example.spmapp.Models.BarChartBar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartFactory {
    //creating bar chart
    Context context;

    public ChartFactory(Context context) {
        this.context = context;
    }

    private BarChart configureChartAppearance(BarChart chart) {
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(12*3600f);
        leftAxis.setValueFormatter(new BarYAxisValueFormatter());

        chart.getAxisRight().setEnabled(false);

        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return chart;
    }

    public BarDataSet createBarDataSet(ArrayList<BarChartBar> bars, String title) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < bars.size(); i++) {
            BarEntry barEntry = new BarEntry(i, bars.get(i).getDuration());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        if (title.equals("Sleep")) {
            barDataSet.setColor(Color.BLUE);
        } else if (title.equals("Downtime")) {
            barDataSet.setColor(Color.YELLOW);
        } else {
            barDataSet.setColor(Color.MAGENTA);
        }
        return barDataSet;
    }

    public BarChart createBarChart(List<IBarDataSet> dataSets) {
        BarData data = new BarData(dataSets);
        BarChart barChart = new BarChart(context);
        barChart = configureChartAppearance(barChart);

        float barSpace = 0.02f;
        float barWidth = 0.4f;
        float groupSpace = 1f - ((barSpace + barWidth) * dataSets.size());

        barChart.setData(data);
        barChart.getBarData().setBarWidth(barWidth);
        barChart.groupBars(0, groupSpace, barSpace);

        return barChart;
    }
}
