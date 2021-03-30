package com.example.spmapp.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spmapp.R;
import com.example.spmapp.Services.DataService;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ListView statListView;
    ListView tipsListView;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        statListView = (ListView)findViewById(R.id.statsListView);
        tipsListView = (ListView)findViewById(R.id.tipsListView);
        loadStats();
        loadTips();
    }

    public void logDataTapped(View view) {
        Intent logPicker = new Intent(this, LogPickerActivity.class);
        startActivity(logPicker);
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