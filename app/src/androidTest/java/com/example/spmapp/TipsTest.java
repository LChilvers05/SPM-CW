package com.example.spmapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TipsTest {

    private Context context;
    private MainDatabase db;
    private SleepDao sleepDao;
    private ScreenDao screenDao;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, MainDatabase.class).build();
        sleepDao = db.sleepDao();
        screenDao = db.screenDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    public String[] return3Tips(){ // returns an array of all 3 tips
        String tip1 = sleepingTip();
        String tip2 = screentimeTip();
        String tip3 = sleepScreenGapTip();
        String[] tips = {tip1, tip2, tip3};
        return tips;
    }

    public String sleepingTip(){ // makes a tip to do with sleeping
        long currentTime = 1600650000;
        boolean wasSleepBetter = isSleepBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(12)); // get a random tip number
        if (!wasSleepBetter){ // if the sleep has gotten worse
            return "This is a tip";
        }
        return "This is a congratulations";
    }

    public String screentimeTip(){
        long currentTime = 1600650000;
        boolean wasScreenBetter = isScreenBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(4));
        if (!wasScreenBetter){
            return "This is a tip";
        }
        return "This is a congratulations";
    }

    public String sleepScreenGapTip(){
        long currentTime = 1600650000;
        boolean wasGapBetter = isSleepScreenGapBetter(currentTime - 604800L, currentTime);
        int tipNumber = (int) Math.floor(Math.random()*(4));
        if (!wasGapBetter){
            return "This is a tip";
        }
        return "This is a congratulations";
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

    @Test // Test 1, tests that the lengths are correctly calculated form start and end times
    public void testTips() {

        Screen s1 = new Screen(1600000000, 1600003600);
        Screen s2 = new Screen(1600007200, 1600009000);
        Screen s3 = new Screen(1600030000, 1600033600);

        Sleep s4 = new Sleep(1600000000, 1600050000, 5);
        Sleep s5 = new Sleep(1600150000, 1600200000, 5);
        Sleep s6 = new Sleep(1600250000, 1600300000, 5);

        screenDao.insertScreen(s1);
        screenDao.insertScreen(s2);
        screenDao.insertScreen(s3);

        sleepDao.insertSleep(s4);
        sleepDao.insertSleep(s5);
        sleepDao.insertSleep(s6);

        String[] autoResponse = return3Tips();
        String sleepingTip = "This is a tip";
        String screenTip = "This is a congratulations";
        String screenGapTip = "This is a congratulations";
        String[] calcResponse = {sleepingTip, screenTip, screenGapTip};
        assertThat(calcResponse, equalTo(autoResponse));
    }
}
