package com.example.spmapp;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.ChartSession;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;
import com.example.spmapp.Services.ChartFactory;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ChartTest {

    private Context context;
    private MainDatabase db;
    private SleepDao sleepDao;
    private ScreenDao screenDao;
    private final int hour = 3600;

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

    //TEST #1: Closest screen time to sleep is fetched for bar chart
    private Screen getScreenTimeClosestToSleep(long sleepStart) {
        Screen[] manualScreens
                = screenDao.getScreenWithEndBetween(sleepStart - 24* Constants.HOUR, sleepStart);

        //gets the screen time closest to sleep by comparing end times
        Screen maxMan = null;
        for(Screen screen : manualScreens){
            if (maxMan == null) { maxMan = screen; }
            if ((screen.endTime > maxMan.endTime && screen.endTime < sleepStart)) {
                maxMan = screen;
            }
        }
        return maxMan;
    }

    @Test
    public void testNearestScreenTimeToSleep() {
        //sleep to test with
        Sleep sleep = new Sleep(General.getUnixTime(), General.getUnixTime() + 8*hour, 5);
        sleepDao.insertSleep(sleep);
        //screen to be closest to sleep
        Screen closest = new Screen(General.getUnixTime() - 2000, General.getUnixTime() - 1000);
        //not closest
        Screen wrongOne = new Screen(General.getUnixTime() - 1500, General.getUnixTime() - 1400);
        Screen wrongTwo = new Screen(General.getUnixTime() + 10, General.getUnixTime() + 20000);
        Screen wrongThree = new Screen(General.getUnixTime() - 3600, General.getUnixTime() - 3000);
        Screen wrongFour = new Screen(General.getUnixTime() + 1500, General.getUnixTime() + 3600);

        screenDao.insertScreen(closest);
        screenDao.insertScreen(wrongOne);
        screenDao.insertScreen(wrongTwo);
        screenDao.insertScreen(wrongThree);
        screenDao.insertScreen(wrongFour);

        Screen result = getScreenTimeClosestToSleep(sleep.startTime);

        assertThat(result.startTime, equalTo(closest.startTime));
        assertThat(result.endTime, equalTo(closest.endTime));
    }

    //TEST #2: Line chart processes screen times correctly to create step chart
    @Test
    public void testLineChartScreensOrder() {
        ChartFactory chartFactory = new ChartFactory(context);
        ArrayList<ChartSession> sessions = new ArrayList<>();
        sessions.add(new ChartSession(1, 10));
        sessions.add(new ChartSession(3, 5));
        sessions.add(new ChartSession(8, 11));
        sessions.add(new ChartSession(14, 16));
        sessions.add(new ChartSession(17, 20));
        sessions.add(new ChartSession(19, 23));

        LineDataSet dataSet = chartFactory.createLineDataSet(sessions);
        int count = dataSet.getEntryCount();

        assertThat(count, equalTo(18));

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(dataSet.getEntryForIndex(i));
        }

        //when there are two ints the same in a row, then there has been a step in the chart
        //(1, 11, 14, 16, 17, 23)
        int[] order = {1, 1, 3, 5, 8, 10, 11, 11, 14, 14, 16, 16, 17, 17, 19, 20, 23, 23};
        for (int i = 0; i < order.length; i++) {
            int num = order[i];
            Entry entry = entries.get(i);

            assertThat((int)entry.getX(), equalTo(num));
        }
    }

    
}
