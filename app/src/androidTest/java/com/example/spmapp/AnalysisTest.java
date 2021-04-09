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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AnalysisTest {

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

    // copying the required functions
    public String createAverageSleepLengthChange(long startTime, long endTime){
        long firstHalfAverage = calculateAverageLength(sleepDao.getSleepStartsBetween(startTime, startTime + (endTime - startTime)/2), sleepDao.getSleepEndsBetween(startTime, startTime + (endTime - startTime)/2));
        long secondHalfAverage = calculateAverageLength(sleepDao.getSleepStartsBetween(startTime + (endTime - startTime)/2, endTime), sleepDao.getSleepEndsBetween(startTime + (endTime - startTime)/2, endTime));
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

    public String getAverageScreenTime(long startTime, long endTime){
        float totalSleepTime = (float)calculateSum(screenDao.getScreenStartsBetween(startTime, endTime), screenDao.getScreenEndsBetween(startTime, endTime));
        float timePeriod = (float)(endTime - startTime);
        float percentage = totalSleepTime/timePeriod;
        return("Daily screentime average: " + Math.round(24*percentage * 100.0) / 100.0 + " hours.");
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

    private long[] calculateLengths(long[] startTimes, long[] endTimes){
        long[] lengths = new long[startTimes.length];
        for (int i = 0; i < startTimes.length; i++){
            lengths[i] = endTimes[i] - startTimes[i];
        } // returns an array of the lengths given array of start and end times
        return lengths;
    }

    private long calculateSum(long[] startTimes, long[] endTimes){
        long[] lengths = calculateLengths(startTimes, endTimes);

        long lengthSum = 0;
        for (long time : lengths){
            lengthSum += time;
        }
        return lengthSum;
    }

    @Test // Test 1, tests that the lengths are correctly calculated form start and end times
    public void testLengthCalculation(){
        Screen s1 = new Screen(10000, 30000);
        Screen s2 = new Screen(1600000000, 1600003600);
        Screen s3 = new Screen(1600007200, 1600009000);
        Screen s4 = new Screen(1600030000, 1600033600);
        Screen s5 = new Screen(1600039000, 1600040000);
        Screen s6 = new Screen(1600070000, 1600073600);
        Screen s7 = new Screen(1800000000, 1800003600);

        screenDao.insertScreen(s1);
        screenDao.insertScreen(s2);
        screenDao.insertScreen(s3);
        screenDao.insertScreen(s4);
        screenDao.insertScreen(s5);
        screenDao.insertScreen(s6);
        screenDao.insertScreen(s7);
        // tests on the 5 middle screens
        long[] startTimes = screenDao.getScreenStartsBetween(1600000000, 1600073600);
        long[] endTimes = screenDao.getScreenEndsBetween(1600000000, 1600073600);
        long[] lengths = calculateLengths(startTimes, endTimes);
        long[] manualLengths = {3600, 1800, 3600, 1000, 3600};
        assertThat(lengths, equalTo(manualLengths));
    }

    @Test // Test 2, tests that the average change can be calculated
    public void testAverageLengthChange(){
        Sleep s1 = new Sleep(10000, 30000, 5);
        Sleep s2 = new Sleep(1600000000, 1600050000, 5);
        Sleep s3 = new Sleep(1600100000, 1600140000, 5);
        Sleep s4 = new Sleep(1600250000, 1600280000, 5);
        Sleep s5 = new Sleep(1600400000, 1600450000, 5);
        Sleep s6 = new Sleep(1600500000, 1600530000, 5);
        Sleep s7 = new Sleep(1600600000, 1600630000, 5);
        Sleep s8 = new Sleep(1800000000, 1800003600, 5);

        sleepDao.insertSleep(s1);
        sleepDao.insertSleep(s2);
        sleepDao.insertSleep(s3);
        sleepDao.insertSleep(s4);
        sleepDao.insertSleep(s5);
        sleepDao.insertSleep(s6);
        sleepDao.insertSleep(s7);
        sleepDao.insertSleep(s8);
        // tests on the 6 middle sleeps
        long firstHalfAverage = (50000 + 40000 + 30000) / 3;
        long secondHalfAverage = (50000 + 30000 + 30000) / 3;
        String autoResponse = createAverageSleepLengthChange(1600000000, 1600630000);
        String calcResponse = "Your average sleep length has decreased by " + (firstHalfAverage - secondHalfAverage)/60 + " minutes";
        assertThat(55L, equalTo((firstHalfAverage - secondHalfAverage)/60));
        assertThat(autoResponse, equalTo(calcResponse));
    }

    @Test // Test 3, tests screen time average
    public void testScreenTimeAverage(){

        Screen s1 = new Screen(10000, 30000);
        Screen s2 = new Screen(1600000000, 1600003600);
        Screen s3 = new Screen(1600007200, 1600009000);
        Screen s4 = new Screen(1600030000, 1600033600);
        Screen s5 = new Screen(1600039000, 1600040000);
        Screen s6 = new Screen(1600070000, 1600073600);
        Screen s7 = new Screen(1800000000, 1800003600);

        screenDao.insertScreen(s1);
        screenDao.insertScreen(s2);
        screenDao.insertScreen(s3);
        screenDao.insertScreen(s4);
        screenDao.insertScreen(s5);
        screenDao.insertScreen(s6);
        screenDao.insertScreen(s7);

        String autoResponse = getAverageScreenTime(1600000000, 1600073600);
        float totalOnScreen = 3600 + 1800 + 3600 + 1000 + 3600;
        float timePeriod = 73600;
        float percentage = totalOnScreen/timePeriod;
        String manualResponse = "Daily screentime average: " + Math.round(24*percentage * 100.0) / 100.0 + " hours.";
        assertThat(4.43, equalTo(Math.round(24*percentage * 100.0) / 100.0));
        assertThat(autoResponse, equalTo(manualResponse));
    }
}
