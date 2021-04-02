package com.example.spmapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.spmapp.Services.ChartFactory;
import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.BarChartBar;
import com.example.spmapp.R;
import com.example.spmapp.ViewModels.HomeActivityViewModel;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class HomeActivity extends AppCompatActivity {

    RelativeLayout chartView;
    ListView statListView;
    ListView tipsListView;

    HomeActivityViewModel viewModel;
    ChartFactory chartFactory;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chartView = findViewById(R.id.chartView);
        statListView = findViewById(R.id.statsListView);
        tipsListView = findViewById(R.id.tipsListView);

        viewModel = new HomeActivityViewModel(getApplication(), this);
        chartFactory = new ChartFactory(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCharts();
        loadStats();
        loadTips();
    }

    public void logDataTapped(View view) {
        Intent logPicker = new Intent(this, LogPickerActivity.class);
        startActivity(logPicker);
    }

    private void loadCharts() {
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        long endTime = General.getUnixTime(); //midnight
        long startTime = endTime - (8*Constants.DAY);

        //create sleep bars
        ArrayList<BarChartBar> chartSleeps
                = viewModel.getDataForDataSet(startTime, endTime, true);
        //create screen bars
        ArrayList<BarChartBar> chartScreens
                = viewModel.getDataForDataSet(startTime, endTime, false);
        //create gap bars
        ArrayList<BarChartBar> chartGaps
                = viewModel.getChartGapsForDataSet(chartSleeps, chartScreens);

        dataSets.add(chartFactory.createBarDataSet(chartScreens, "Closest Screen Time to Sleep"));
        dataSets.add(chartFactory.createBarDataSet(chartGaps, "Downtime"));
        dataSets.add(chartFactory.createBarDataSet(chartSleeps, "Sleep"));

        //create gap bars
        chartView.removeAllViews();
        chartView.addView(chartFactory.createBarChart(dataSets));
    }

    public void loadStats(){
        //get stats --> array --> update listview
        ArrayList<String> statsArray = new ArrayList<String>();

        //examples of adding statistics
        int screentime = 12;
        statsArray.add("Total screentime: " + screentime);

        int totalSleep = 7;
        statsArray.add("Total sleep: " + totalSleep);

        //populates listview with stats
        ArrayAdapter<String> statsArrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_listview_item, R.id.itemTextViewContents, statsArray);
        statListView.setAdapter(statsArrayAdapter);
    }

    public void loadTips(){
        //generate tips --> array --> update listview
        ArrayList<String> tipsArray = new ArrayList<String>();

        //examples of adding tips
        String tip1 = "Sleep more as this will really help you with achieving all your goals blah blah blah";
        tipsArray.add(tip1);

        String tip2 = "Sleep even more";
        tipsArray.add(tip2);

        //populates listview with tips
        ArrayAdapter<String> tipsArrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_listview_item, R.id.itemTextViewContents, tipsArray);
        tipsListView.setAdapter(tipsArrayAdapter);
    }
}