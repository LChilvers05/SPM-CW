package com.example.spmapp.Models;

import android.app.Application;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;

import java.time.Instant;

public class Tips {

    private final SleepDao sleepDao;
    private final ScreenDao screenDao;

    public Tips(Application application) {
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();
    }

    public String[] return3Tips(){ // returns an array of all 3 tips
        String tip1 = sleepingTip();
        String tip2 = screentimeTip();
        String tip3 = sleepScreenGapTip();
        String[] tips = {tip1, tip2, tip3};
        return tips;
    }

    public String sleepingTip(){ // makes a tip to do with sleeping
        long currentTime = Instant.now().getEpochSecond();
        boolean wasSleepBetter = isSleepBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(4)); // get a random tip number
        if (!wasSleepBetter){ // if the sleep has gotten worse
            switch (tipNumber) { // return that tip
                case 0:
                    return "You slept less recently, try to keep a regular sleeping routine.";
                case 1:
                    return "You slept less recently, try to wake up at the same time every day.";
                case 2:
                    return "You slept less recently, try to wind down before by, e.g. a book or bath.";
                case 3:
                    return "You slept less recently, try to make sure your bedroom is dark and quiet.";
            }
        }
        return "You've slept for longer recently, keep it up!";
    }

    public String screentimeTip(){
        long currentTime = Instant.now().getEpochSecond();
        boolean wasScreenBetter = isScreenBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(4));
        if (!wasScreenBetter){
            switch (tipNumber) {
                case 0:
                    return "You're screen time is higher recently, try keeping your phone out of reach.";
                case 1:
                    return "You're screen time is higher recently, try putting notifications on silence.";
                case 2:
                    return "You're screen time is higher recently, try deleting apps that aren't important.";
                case 3:
                    return "You're screen time is higher recently, try turning off your phone when it's unneeded.";
            }
        }
        return "You've spent less time on screens recently, keep it up!";
    }

    public String sleepScreenGapTip(){
        long currentTime = Instant.now().getEpochSecond();
        boolean wasGapBetter = isSleepScreenGapBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(4));
        if (!wasGapBetter){
            switch (tipNumber) {
                case 0:
                    return "You're sleep-screen gap is lower, try to keep your phone away from bed.";
                case 1:
                    return "You're sleep-screen gap is lower, try turning off your phone before bed.";
                case 2:
                    return "You're sleep-screen gap is lower, try using a bedside alarm clock instead of a phone.";
                case 3:
                    return "You're sleep-screen gap is lower, try to do something, e.g. read, before bed.";
            }
        }
        return "You're off your screen for longer before bed recently, keep it up!";
    }

    public boolean isSleepScreenGapBetter(long startTime, long endTime){
        long firstHalfSum = calculateGapSums(startTime, startTime + (endTime - startTime)/2);
        long secondHalfSum = calculateGapSums(startTime + (endTime - startTime)/2, endTime);
        if (firstHalfSum > secondHalfSum){
            return false;
        } else {
            return true;
        }
    }

    public boolean isSleepBetter(long startTime, long endTime){
        long firstHalfSum = calculateSum(sleepDao.getSleepStartsBetween(startTime, startTime + (endTime - startTime)/2), sleepDao.getSleepEndsBetween(startTime, startTime + (endTime - startTime)/2));
        long secondHalfSum = calculateSum(sleepDao.getSleepStartsBetween(startTime + (endTime - startTime)/2, endTime), sleepDao.getSleepEndsBetween(startTime + (endTime - startTime)/2, endTime));
        if (firstHalfSum > secondHalfSum){
            return false;
        } else {
            return true;
        }
    }

    public boolean isScreenBetter(long startTime, long endTime){
        long firstHalfSum = calculateSum(screenDao.getScreenStartsBetween(startTime, startTime + (endTime - startTime)/2), screenDao.getScreenEndsBetween(startTime, startTime + (endTime - startTime)/2));
        long secondHalfSum = calculateSum(screenDao.getScreenStartsBetween(startTime + (endTime - startTime)/2, endTime), screenDao.getScreenEndsBetween(startTime + (endTime - startTime)/2, endTime));
        if (firstHalfSum < secondHalfSum){
            return false;
        } else {
            return true;
        }
    }

    private long calculateGapSums(long startTime, long endTime){ // gets the sum of the time between sleep and the last screen
        Sleep[] sleeps = sleepDao.getSleepWithStartBetween(startTime, endTime);
        Screen[] screens = screenDao.getScreenWithStartBetween(startTime, endTime);
        long gapSum = 0;
        for (Sleep sleep : sleeps){
            long shortestGap = 9223372036854758070L;
            for (Screen screen : screens){
                if ((sleep.startTime - screen.endTime) < shortestGap && (sleep.startTime - screen.endTime) >= 0){
                    shortestGap = sleep.startTime - screen.endTime;
                }
            }
            gapSum += shortestGap;
        }
        return gapSum;
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
