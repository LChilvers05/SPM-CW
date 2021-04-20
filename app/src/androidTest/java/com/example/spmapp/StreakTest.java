package com.example.spmapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Models.Sleep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class StreakTest {

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

    public int getStreak(long goal){
        boolean streakGoing = true;
        int streak = 0;
        long currentTime = 1618848000; //5pm 19th April 2021
        long lastDayEnd = (currentTime - (currentTime % 86400)) + 54000;
        while(streakGoing){
            long sum = calculateSum(sleepDao.getSleepStartsBetween(lastDayEnd - 86400, lastDayEnd), sleepDao.getSleepEndsBetween(lastDayEnd - 86400, lastDayEnd));
            System.out.println("hi");
            if (sum >= goal) {
                streak++;
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

    @Test
    public void testStreakCalculator(){

        Sleep s1 = new Sleep(1618434000, 1618470000, 5);
        //10pm 14th (April 2021) - 8am 15th
        Sleep s2 = new Sleep(1618524000, 1618549200, 5);
        //11pm 15h - 6am 16th
        Sleep s3 = new Sleep(1618603200, 1618650000, 5);
        //9pm 16th - 10am 17th
        Sleep s4 = new Sleep(1618700400, 1618722000, 5);
        //12 midnight 17th - 6am 18th
        Sleep s5 = new Sleep(1618736400, 1618743600, 5);
        //10am 18th - 12am 18th to simulate a nap
        Sleep s6 = new Sleep(1618779600, 1618812000, 5);
        //10pm 18th - 7am 19th

        sleepDao.insertSleep(s1);
        sleepDao.insertSleep(s2);
        sleepDao.insertSleep(s3);
        sleepDao.insertSleep(s4);
        sleepDao.insertSleep(s5);
        sleepDao.insertSleep(s6);

        int streak = getStreak(28800); // streak is 8 hours
        assertThat(streak, equalTo(3));

    }
}
