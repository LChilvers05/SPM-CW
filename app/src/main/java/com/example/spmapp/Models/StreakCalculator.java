package com.example.spmapp.Models;

import android.app.Application;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;

import java.time.Instant;

public class StreakCalculator {

    private final SleepDao sleepDao;
    private final ScreenDao screenDao;

    public StreakCalculator(Application application, long goal) {
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();
    }

    // takes in the goal/target as long seconds
    public int getStreak(long goal){
        boolean streakGoing = true;
        int streak = 0;
        long currentTime = Instant.now().getEpochSecond(); // gets the current time
        long lastDayEnd = (currentTime - (currentTime % 86400)) + 54000; // gets 2pm UTC of the current day
        while(streakGoing){
            long sum = calculateSum(sleepDao.getSleepStartsBetween(lastDayEnd - 86400, lastDayEnd), sleepDao.getSleepEndsBetween(lastDayEnd - 86400, lastDayEnd));
            if (sum >= goal) {
                streak++; // increments streak if enough sleep and goes to next day
                lastDayEnd = lastDayEnd - 86400;
            } else {
                streakGoing = false;
            }
        }
        return streak;
    }

    private long calculateSum(long[] startTimes, long[] endTimes){
        long[] lengths = calculateLengths(startTimes, endTimes);

        long lengthSum = 0;
        for (long time : lengths){
            lengthSum += time;
        }
        return lengthSum;
    }

    private long[] calculateLengths(long[] startTimes, long[] endTimes){
        long[] lengths = new long[startTimes.length];
        for (int i = 0; i < startTimes.length; i++){
            lengths[i] = endTimes[i] - startTimes[i];
        } // returns an array of the lengths given array of start and end times
        return lengths;
    }
}
