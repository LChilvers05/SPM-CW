package com.example.spmapp.ViewModels;

import android.annotation.SuppressLint;
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

@RequiresApi(api = Build.VERSION_CODES.Q)
public class HomeActivityViewModel extends AndroidViewModel {

    Context context;

    public HomeActivityViewModel(@NonNull Application application, Context context) {
        super(application);
        this.context = context;
    }

    public ArrayList<BarChartBar> getDataForDataSet(long startTime, long endTime, Boolean forSleep) {
        //contains BarChatBar objects which simply holds start and end time
        ArrayList<BarChartBar> dataSetData = new ArrayList<>();
        //get the amount of days chart will display
        int days = (int) (endTime - startTime)/Constants.DAY;
        //for each day request a bar of data
        for (int i = 0; i < days; i++) {
            long reqStart = startTime + (i*Constants.DAY);
            long reqEnd = startTime + ((i+1)*Constants.DAY);

            BarChartBar bar = getBar(reqStart, reqEnd, forSleep);
            dataSetData.add(bar);
        }
        return dataSetData;
    }

    private BarChartBar getBar(long reqStart, long reqEnd, Boolean forSleep) {
        Sleep[] daysSleeps = DataService.shared().getSleepsBetweenPeriod(reqStart, reqEnd);
        //get the longest sleep in this day
        Sleep longestSleep = null;
        long longestDur = 0;
        for (Sleep daysSleep : daysSleeps) {
            long dur = daysSleep.endTime - daysSleep.startTime;
            if (dur > longestDur) {
                longestDur = dur;
                longestSleep = daysSleep;
            }
        }
        //only show screen if user logged sleep (because closest screen to sleep)
        if (longestSleep != null) {
            if (forSleep) {
                return new BarChartBar(longestSleep.startTime, longestSleep.endTime);
            }
            //for screen
            Screen daysScreen = DataService.shared().getScreenTimeClosestToSleep(context, longestSleep.startTime);
            if (daysScreen != null) {
                return new BarChartBar(daysScreen.startTime, daysScreen.endTime);
            }
        }
        //return empty bar with 0 duration
        return new BarChartBar(reqStart, reqStart);
    }

    public ArrayList<BarChartBar> getChartGapsForDataSet(ArrayList<BarChartBar> chartSleeps, ArrayList<BarChartBar> chartScreens) {
        ArrayList<BarChartBar> chartGaps = new ArrayList<>();
        //create a list of bars of gaps from end of last screen to start of sleep
        if (chartSleeps.size() == chartScreens.size()) {
            for (int i = 0; i < chartSleeps.size(); i++) {
                BarChartBar chartSleep = chartSleeps.get(i);
                BarChartBar chartScreen = chartScreens.get(i);
                if (chartSleep.getDuration() == 0 || chartScreen.getDuration() == 0) {
                    chartGaps.add(new BarChartBar(chartScreen.getEnd(), chartScreen.getEnd()));
                } else {
                    chartGaps.add(new BarChartBar(chartScreen.getEnd(), chartSleep.getStart()));
                }
            }
        }
        return chartGaps;
    }
}
