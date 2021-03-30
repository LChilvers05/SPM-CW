package com.example.spmapp.ViewModels;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.BarChartBar;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;
import com.example.spmapp.Services.DataService;

import java.util.ArrayList;

public class HomeActivityViewModel extends AndroidViewModel {

    Context context;

    public HomeActivityViewModel(@NonNull Application application, Context context) {
        super(application);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<BarChartBar> getDataForDataSet(long startTime, long endTime, Boolean forSleep) {
        ArrayList<BarChartBar> dataSetData = new ArrayList<>();

        int days = (int) (endTime - startTime)/Constants.DAY;

        for (int i = 0; i < days; i++){
            long reqStart = startTime + (i*Constants.DAY);
            long reqEnd = startTime + ((i+1)*Constants.DAY);

            dataSetData.add(getBar(reqStart, reqEnd, forSleep, i));
        }

        return dataSetData;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private BarChartBar getBar(long reqStart, long reqEnd, Boolean forSleep, int day) {
        long sleepStart = General.getUnixTime() - ((8-day)*Constants.DAY);
        long sleepEnd;
        Sleep[] daysSleeps = DataService.shared().getSleepsBetweenPeriod(reqStart, reqEnd);
        if (daysSleeps.length != 0) {
            sleepStart = daysSleeps[0].startTime;
            sleepEnd = daysSleeps[daysSleeps.length - 1].endTime;

            if (forSleep) {
                return new BarChartBar(sleepStart, sleepEnd);
            }
        }
        //for screen
        Screen daysScreen = DataService.shared().getScreenTimeClosestToSleep(context, sleepStart);
        return new BarChartBar(daysScreen.startTime, daysScreen.endTime);
    }
}