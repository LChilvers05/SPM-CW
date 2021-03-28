package com.example.spmapp.Models;

import android.app.Application;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;

public class CreateAnalysis {

    private final SleepDao sleepDao;
    private final ScreenDao screenDao;
    private final long startTime;
    private final long endTime;

    private CreateAnalysis(Application application, long startTime, long endTime) {
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String createAverageSleepLengthChange(){
        long[] firstHalf = sleepDao.getSleepLengthsBetween(startTime, endTime/2);
        long[] secondHalf = sleepDao.getSleepLengthsBetween(endTime/2, endTime);
        long firstHalfSum = 0;
        long secondHalfSum = 0;
        for (long time : firstHalf){
            firstHalfSum += time;
        }
        long firstHalfAverage = firstHalfSum / firstHalf.length;
        for (long time : secondHalf){
            secondHalfSum += time;
        }
        long secondHalfAverage = secondHalfSum / secondHalf.length;
        if (firstHalfAverage > secondHalfAverage){
            return("Your average sleep length has decreased by" + (firstHalfAverage - secondHalfAverage)/60 + "minutes");
        }
        else if (firstHalfAverage < secondHalfAverage){
            return("Your average sleep length has increased by" + (secondHalfAverage - firstHalfAverage)/60 + "minutes");
        } else {
            return("Your average sleep length has stayed exactly the same... impressive");
        }
    }

    public String createAverageScreenTimeTotalChange(){
        long[] firstHalf = screenDao.getScreenLengthsBetween(startTime, endTime/2);
        long[] secondHalf = screenDao.getScreenLengthsBetween(endTime/2, endTime);
        long firstHalfSum = 0;
        long secondHalfSum = 0;
        for (long time : firstHalf){
            firstHalfSum += time;
        }
        for (long time : secondHalf){
            secondHalfSum += time;
        }
        if (firstHalfSum < secondHalfSum){
            return("You spent" + (secondHalfSum - firstHalfSum)/60 + "more minutes on screens in the second half");
        } else if (firstHalfSum > secondHalfSum){
            return("You spent" + (firstHalfSum - secondHalfSum)/60 + "fewer minutes on screens in the second half");
        } else {
            return("You somehow spent the same amount of time in both halves...");
        }
    }
}
