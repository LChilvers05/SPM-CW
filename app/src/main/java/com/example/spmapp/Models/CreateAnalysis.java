package com.example.spmapp.Models;

import android.app.Application;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;

public class CreateAnalysis {

    private final SleepDao sleepDao;
    private final ScreenDao screenDao;

    public CreateAnalysis(Application application) {
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();
    }

    private long[] calculateLengths(long[] startTimes, long[] endTimes){
        long[] lengths = new long[startTimes.length];
        for (int i = 0; i < startTimes.length; i++){
            lengths[i] = endTimes[i] - startTimes[i];
        } // returns an array of the lengths given array of start and end times
        return lengths;
    }

    private long calculateAverageLength(long[] startTimes, long[] endTimes){
        long[] lengths = calculateLengths(startTimes, endTimes);

        long lengthSum = 0;
        for (long time : lengths){
            lengthSum += time;
        } // returns the average lengths
        if(lengths.length != 0) {
            return (lengthSum / lengths.length);
        }else{
            return (lengthSum);
        }
    }

    public String createAverageSleepLengthChange(long startTime, long endTime){
        long firstHalfAverage = calculateAverageLength(sleepDao.getSleepStartsBetween(startTime, (endTime - startTime)/2), sleepDao.getSleepEndsBetween(startTime, (endTime - startTime)/2));
        long secondHalfAverage = calculateAverageLength(sleepDao.getSleepStartsBetween((endTime - startTime)/2, endTime), sleepDao.getSleepEndsBetween((endTime - startTime)/2, endTime));
        // outputs a message depending on the difference between first and second half of the data
        if (firstHalfAverage > secondHalfAverage){
            return("Your average sleep length has decreased by " + (firstHalfAverage - secondHalfAverage)/60 + " minutes");
        }
        else if (firstHalfAverage < secondHalfAverage){
            return("Your average sleep length has increased by " + (secondHalfAverage - firstHalfAverage)/60 + " minutes");
        } else {
            return("Your average sleep length has stayed exactly the same... impressive");
        }
    }

    private long calculateSum(long[] startTimes, long[] endTimes){
        long[] lengths = calculateLengths(startTimes, endTimes);

        long lengthSum = 0;
        for (long time : lengths){
            lengthSum += time;
        }
        return lengthSum;
    }

    public String createAverageScreenTimeTotalChange(long startTime, long endTime){
        long firstHalfSum = calculateSum(screenDao.getScreenStartsBetween(startTime, (endTime - startTime)/2), screenDao.getScreenEndsBetween(startTime, (endTime - startTime)/2));
        long secondHalfSum = calculateSum(screenDao.getScreenStartsBetween((endTime - startTime)/2, endTime), screenDao.getScreenEndsBetween((endTime - startTime)/2, endTime));
        if (firstHalfSum < secondHalfSum){
            return("You spent " + (secondHalfSum - firstHalfSum)/60 + " more minutes on screens in the second half");
        } else if (firstHalfSum > secondHalfSum){
            return("You spent " + (firstHalfSum - secondHalfSum)/60 + " fewer minutes on screens in the second half");
        } else {
            return("You somehow spent the same amount of time in both halves...");
        }
    }

    public String getAverageSleepTime(long startTime, long endTime){
        float totalSleepTime = (float)calculateSum(sleepDao.getSleepStartsBetween(startTime, endTime), sleepDao.getSleepEndsBetween(startTime, endTime));
        float timePeriod = (float)(endTime - startTime);
        float percentage = totalSleepTime/timePeriod;
        return("You slept an average of " + 24*percentage + " hours every day over this time period");
    }

    public String getAverageScreenTime(long startTime, long endTime){
        float totalSleepTime = (float)calculateSum(screenDao.getScreenStartsBetween(startTime, endTime), screenDao.getScreenEndsBetween(startTime, endTime));
        float timePeriod = (float)(endTime - startTime);
        float percentage = totalSleepTime/timePeriod;
        return("You looked at a screen an average of " + 24*percentage + " hours every day over this time period");
    }

    public String checkCorrelation(long startTime, long endTime){
        long firstHalfScreenSum = calculateSum(screenDao.getScreenStartsBetween(startTime, (endTime - startTime)/2), screenDao.getScreenEndsBetween(startTime, (endTime - startTime)/2));
        long secondHalfScreenSum = calculateSum(screenDao.getScreenStartsBetween((endTime - startTime)/2, endTime), screenDao.getScreenEndsBetween((endTime - startTime)/2, endTime));
        long firstHalfSleepSum = calculateSum(sleepDao.getSleepStartsBetween(startTime, (endTime - startTime)/2), sleepDao.getSleepEndsBetween(startTime, (endTime - startTime)/2));
        long secondHalfSleepSum = calculateSum(sleepDao.getSleepStartsBetween((endTime - startTime)/2, endTime), sleepDao.getSleepEndsBetween((endTime - startTime)/2, endTime));
        if (firstHalfScreenSum > secondHalfScreenSum && firstHalfSleepSum > secondHalfSleepSum){
            return("Your screen time decreased, but so did your sleep...");
        } else if (firstHalfScreenSum > secondHalfScreenSum && firstHalfSleepSum < secondHalfSleepSum){
            return("Your screen time decreased, and your sleep time increased!");
        } else if (firstHalfScreenSum < secondHalfScreenSum && firstHalfSleepSum > secondHalfSleepSum){
            return("Your screen time increased, and your sleep time decreased... curious.");
        } else if (firstHalfScreenSum < secondHalfScreenSum && firstHalfSleepSum < secondHalfSleepSum){
            return("Your screen time increased, and your sleep time did too... huh.");
        }
        return("You managed to have identical data over the two halves, impressive.");
    }

    public void deleteAllFromDatabase(){
        screenDao.deleteAllScreens();
        sleepDao.deleteAllSleeps();
    }
}
