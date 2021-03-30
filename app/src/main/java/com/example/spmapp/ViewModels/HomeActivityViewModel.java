package com.example.spmapp.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Models.BarChartBar;
import com.example.spmapp.Models.Sleep;
import com.example.spmapp.Services.DataService;

import java.util.ArrayList;

public class HomeActivityViewModel extends AndroidViewModel {

    public HomeActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public ArrayList<BarChartBar> getSleepsForBarChart(long startTime, long endTime) {
        ArrayList<BarChartBar> chartSleeps = new ArrayList<>();

        int days = (int) (endTime - startTime)/Constants.DAY;

        for (int i = 0; i < days; i++){
            long reqStart = startTime + (i*Constants.DAY);
            long reqEnd = startTime + ((i+1)*Constants.DAY);

            Sleep[] daysSleeps = DataService.shared().getSleepsBetweenPeriod(reqStart, reqEnd);
            if (daysSleeps.length != 0) {
                long start = daysSleeps[0].startTime;
                long end = daysSleeps[daysSleeps.length - 1].endTime;
                chartSleeps.add(new BarChartBar(start, end));
            }
        }

        return chartSleeps;
    }
}
