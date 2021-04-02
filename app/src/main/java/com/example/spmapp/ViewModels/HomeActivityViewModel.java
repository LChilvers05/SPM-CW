package com.example.spmapp.ViewModels;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Models.ChartSession;
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

    public ArrayList<ChartSession> getDataForBarDataSet(long startTime, long endTime, Boolean forSleep) {
        //contains BarChatBar objects which simply holds start and end time
        ArrayList<ChartSession> dataSetData = new ArrayList<>();
        //get the amount of days chart will display
        int days = (int) (endTime - startTime)/Constants.DAY;
        //for each day request a bar of data
        for (int i = 0; i < days; i++) {
            long reqStart = startTime + (i*Constants.DAY);
            long reqEnd = startTime + ((i+1)*Constants.DAY);

            ChartSession bar = getBar(reqStart, reqEnd, forSleep);
            dataSetData.add(bar);
        }
        return dataSetData;
    }

    private ChartSession getBar(long reqStart, long reqEnd, Boolean forSleep) {
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
                return new ChartSession(longestSleep.startTime, longestSleep.endTime);
            }
            //for screen
            Screen daysScreen = DataService.shared().getScreenTimeClosestToSleep(context, longestSleep.startTime);
            if (daysScreen != null) {
                return new ChartSession(daysScreen.startTime, daysScreen.endTime);
            }
        }
        //return empty bar with 0 duration
        return new ChartSession(reqStart, reqStart);
    }

    public ArrayList<ChartSession> getChartGapsForBarDataSet(ArrayList<ChartSession> chartSleeps, ArrayList<ChartSession> chartScreens) {
        ArrayList<ChartSession> chartGaps = new ArrayList<>();
        //create a list of bars of gaps from end of last screen to start of sleep
        if (chartSleeps.size() == chartScreens.size()) {
            for (int i = 0; i < chartSleeps.size(); i++) {
                ChartSession chartSleep = chartSleeps.get(i);
                ChartSession chartScreen = chartScreens.get(i);
                if (chartSleep.getDuration() == 0 || chartScreen.getDuration() == 0) {
                    chartGaps.add(new ChartSession(chartScreen.getEnd(), chartScreen.getEnd()));
                } else {
                    chartGaps.add(new ChartSession(chartScreen.getEnd(), chartSleep.getStart()));
                }
            }
        }
        return chartGaps;
    }

    public ArrayList<ChartSession> getDataForLineDataSet(long startTime, long endTime) {

        ArrayList<ChartSession> dataSetData = new ArrayList<>();
        Screen[] periodScreens = DataService.shared().getScreensBetweenPeriod(startTime, endTime);

        for (Screen screen : periodScreens) {
            dataSetData.add(new ChartSession(screen.startTime, screen.endTime));
        }

        return dataSetData;
    }
}
