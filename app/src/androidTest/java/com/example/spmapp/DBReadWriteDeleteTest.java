package com.example.spmapp;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DBReadWriteDeleteTest {

    private MainDatabase db;
    private SleepDao sleepDao;
    private ScreenDao screenDao;
    private final int hour = 3600;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, MainDatabase.class).build();
        sleepDao = db.sleepDao();
        screenDao = db.screenDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeAndReadSleep() throws Exception {
        //write
        Sleep sleep = new Sleep(General.getUnixTime(), General.getUnixTime() + 8*hour, 5);
        sleepDao.insertSleep(sleep);
        Sleep sleepTwo = new Sleep(General.getUnixTime() + 1, General.getUnixTime() + 4*hour, 2);
        sleepDao.insertSleep(sleepTwo);

        //read
        Sleep[] allSleeps = sleepDao.getAllSleeps();

        //test
        assertThat(allSleeps[0].startTime, equalTo(sleep.startTime));
        assertFalse((allSleeps[1].startTime == sleep.startTime));
        assertThat(allSleeps[0].endTime, equalTo(sleep.endTime));
    }

    @Test
    public void writeAndReadScreen() throws Exception {
        //write
        Screen screen = new Screen(General.getUnixTime(), General.getUnixTime() + 2*hour);
        screenDao.insertScreen(screen);
        Screen screenTwo = new Screen(General.getUnixTime() + 1, General.getUnixTime() + hour);
        screenDao.insertScreen(screenTwo);

        //read
        Screen[] allScreens = screenDao.getAllScreens();

        //test
        assertThat(allScreens[0].startTime, equalTo(screen.startTime));
        assertFalse((allScreens[1].startTime == screen.startTime));
        assertThat(allScreens[0].endTime, equalTo(screen.endTime));
    }

    @Test
    public void deleteSleep() {
        sleepDao.insertSleep(new Sleep(1618526271, 1618579971, 5));
        screenDao.insertScreen(new Screen(1618508271, 1618522671));
        //Fri 16 - Sat 17
        sleepDao.insertSleep(new Sleep(1618596720, 1618639680, 5));
        screenDao.insertScreen(new Screen(1618579200, 1618586280));
        //Sat 17 - Sun 18
        sleepDao.insertSleep(new Sleep(1618698720, 1618741920, 5));
        screenDao.insertScreen(new Screen(1618687200, 1618698000));
        //Sun 18 - Mon 19
        sleepDao.insertSleep(new Sleep(1618780140, 1618816800, 5));
        screenDao.insertScreen(new Screen(1618751520, 1618754940));
        //Mon 19 - Tue 20
        sleepDao.insertSleep(new Sleep(1618866120, 1618891920, 5));
        screenDao.insertScreen(new Screen(1618837320, 1618862520));
        //Tue 20 - Wed 21
        sleepDao.insertSleep(new Sleep(1618958460, 1619013720, 5));
        screenDao.insertScreen(new Screen(1618939620, 1618957620));
        //Wed 21 - Thu 22
        sleepDao.insertSleep(new Sleep(1619041500, 1619091000, 5));
        screenDao.insertScreen(new Screen(1619030820, 1619037900));
        //Thu 22 - Fri 23
        sleepDao.insertSleep(new Sleep(1619129880, 1619173200, 5));
        screenDao.insertScreen(new Screen(1619120580, 1619128020));

        Sleep[] allSleepsBefore = sleepDao.getAllSleeps();

        sleepDao.deleteSleepsBetween(1618526270, 1618596721);

        Sleep[] getAllSleepsAfter = sleepDao.getAllSleeps();

        assertNotEquals(allSleepsBefore.length, getAllSleepsAfter.length);
        assertThat(getAllSleepsAfter.length, equalTo(6));
    }
}
