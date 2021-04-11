package com.example.spmapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.example.spmapp.Models.CreateAnalysis;
import com.example.spmapp.Models.GlobalChartView;
import com.example.spmapp.Services.ChartFactory;
import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.ChartSession;
import com.example.spmapp.R;
import com.example.spmapp.Services.DataService;
import com.example.spmapp.ViewModels.HomeActivityViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class HomeActivity extends AppCompatActivity {

    RelativeLayout chartView;
    ListView statListView;
    ListView tipsListView;
    CreateAnalysis createAnalysis;

    long endTime; //midnight
    long startTime;

    HomeActivityViewModel viewModel;
    ChartFactory chartFactory;

    Chart showingChart;

    Context thisContext;

    private boolean viewingBar = true;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        endTime = General.getUnixTime();
        startTime = endTime - (8*Constants.DAY);
        createAnalysis = new CreateAnalysis(getApplication());
        
        chartView = findViewById(R.id.chartView);
        statListView = findViewById(R.id.statsListView);
        tipsListView = findViewById(R.id.tipsListView);

        thisContext = this;
        viewModel = new HomeActivityViewModel(getApplication(), this);
        chartFactory = new ChartFactory(this);

        DataService.shared().insertAutomaticScreens(this, startTime, endTime);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        //Display dropdown menu
        MenuInflater dropdownMenuInflater = getMenuInflater();
        dropdownMenuInflater.inflate(R.menu.dropdown_menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        //Deals with dropdown menu item selection
        super.onOptionsItemSelected(menuItem);
        switch(menuItem.getItemId()){
            case R.id.privacyLink:
                startActivity(new Intent(this, PrivacyAgreement.class));
                return true;

            case R.id.preferencesLink:
                startActivity(new Intent(this, UserPreferences.class));
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCharts(viewingBar, true);
        loadStats();
        loadTips();
    }


    public void logDataTapped(View view) {
        Intent logPicker = new Intent(this, LogPickerActivity.class);
        startActivity(logPicker);
    }

    private void loadCharts(boolean loadBar, boolean addToView) {

        long endTime = General.getUnixTime(); //midnight
        long startTime = endTime - (8*Constants.DAY);

        if (loadBar) {
            //BAR CHART
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            //create sleep bars
            ArrayList<ChartSession> barChartSleeps
                    = viewModel.getDataForBarDataSet(startTime, endTime, true);
            //create screen bars
            ArrayList<ChartSession> barChartScreens
                    = viewModel.getDataForBarDataSet(startTime, endTime, false);
            //create gap bars
            ArrayList<ChartSession> barChartGaps
                    = viewModel.getChartGapsForBarDataSet(barChartSleeps, barChartScreens);

            dataSets.add(chartFactory.createBarDataSet(barChartScreens, "Screen Time Prior to Sleep"));
            dataSets.add(chartFactory.createBarDataSet(barChartGaps, "Downtime"));
            dataSets.add(chartFactory.createBarDataSet(barChartSleeps, "Sleep"));

            BarChart barChart = chartFactory.createBarChart(dataSets);

            if (addToView) {
                chartView.removeAllViews();
                chartView.addView(barChart);
            }
            showingChart = barChart;
        } else {
            //LINE CHART
            ArrayList<ChartSession> lineChartScreens
                    = viewModel.getDataForLineDataSet(startTime, endTime);

            LineChart lineChart = chartFactory.createLineChart(chartFactory.createLineDataSet(lineChartScreens));

            if (addToView) {
                chartView.removeAllViews();
                chartView.addView(lineChart);
            }
            showingChart = lineChart;
        }
        showingChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {}

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                loadCharts(viewingBar, false);
                GlobalChartView.chartView = showingChart;
                startActivity(new Intent(thisContext, GraphDetail.class));
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    public void chartClicked(View view) {

    }

    public void changeChart(View view) {
        loadCharts(!viewingBar, true);
        viewingBar = !viewingBar;
    }

    public void loadStats(){
        //get stats --> array --> update listview
        ArrayList<String> statsArray = new ArrayList<String>();

        try {
            String averageSleep = createAnalysis.getAverageSleepTime(startTime, endTime);
            statsArray.add(averageSleep);

            String averageSleepChange = createAnalysis.createAverageSleepLengthChange(startTime, endTime);
            statsArray.add(averageSleepChange);

            String averageScreenTime = createAnalysis.getAverageScreenTime(startTime, endTime);
            statsArray.add(averageScreenTime);

            String averageScreenChange = createAnalysis.createAverageScreenTimeTotalChange(startTime, endTime);
            statsArray.add(averageScreenChange);
        }catch(Exception e){
            System.out.println(e);
            statsArray.add("Insufficient data.");
        }

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