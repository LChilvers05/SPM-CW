package com.example.spmapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.spmapp.Models.GlobalChartView;
import com.example.spmapp.R;
import com.github.mikephil.charting.charts.Chart;

public class GraphDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        Chart chartView = GlobalChartView.chartView;
        if(chartView.getParent() != null) {
            ((ViewGroup)chartView.getParent()).removeView(chartView); // <- fix
        }
        RelativeLayout relativeLayout = findViewById(R.id.chartRelativeView);
        relativeLayout.removeAllViews();
        relativeLayout.addView(chartView);
    }


}