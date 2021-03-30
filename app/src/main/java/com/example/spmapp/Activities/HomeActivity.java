package com.example.spmapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.spmapp.ChartFactory;
import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.BarChartBar;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.HomeActivityViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RelativeLayout testChartRL;

    HomeActivityViewModel viewModel;
    ChartFactory chartFactory;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        testChartRL = findViewById(R.id.chart_layout);

        viewModel = new HomeActivityViewModel(getApplication());
        chartFactory = new ChartFactory(this);

        drawCharts();
    }

    public void logDataTapped(View view) {
        Intent logPicker = new Intent(this, LogPickerActivity.class);
        startActivity(logPicker);
    }

    private void drawCharts() {

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();

        ArrayList<BarChartBar> chartSleeps
                = viewModel.getSleepsForBarChart(General.getUnixTime() - (Constants.DAY*8), General.getUnixTime());
        dataSets.add(chartFactory.createBarDataSet(chartSleeps, "Sleep"));

        //TODO: chartScreens
        //TODO: chartGap

        BarChart barChart = chartFactory.createBarChart(dataSets);
        testChartRL.addView(barChart);
    }
}