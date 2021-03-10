package com.example.spmapp;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.spmapp.Helpers.General;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DBReadWriteTest {

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
}
