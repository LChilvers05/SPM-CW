package com.example.spmapp;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.spmapp.Activities.SleepCollectorActivity;
import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.Sleep;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SleepCollectorActivityTest {

    private SleepDao sleepDao;

    @Rule
    public ActivityScenarioRule<SleepCollectorActivity> activityScenarioRule
            = new ActivityScenarioRule<>(SleepCollectorActivity.class);

    @Before
    public void init() {
        this.sleepDao = MainDatabase.getDB(ApplicationProvider.getApplicationContext()).sleepDao();
    }

    private long dateToUnixTime(String dateStr, String timeStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH);
        String timestampStr = dateStr + " " + timeStr;
        return General.getUnixTimeFromDate(formatter.parse(timestampStr));
    }

    @Test
    public void testLog() throws ParseException {
        //INPUT - insert in database with UI
        String startTimeStr = "21:00";
        String endTimeStr = "7:32";
        String dateStr = "13/3/2021";
        onView(withId(R.id.startTimeEntry))
                .perform(replaceText(startTimeStr));
        onView(withId(R.id.endTimeEntry))
                .perform(replaceText(endTimeStr));
        onView(withId(R.id.startDateEntry))
                .perform(replaceText(dateStr));
        onView(withId(R.id.logDataButton)).perform(click());
        //OUTPUT - fetch from database
        long startTimestamp = dateToUnixTime(dateStr, startTimeStr);
        long endTimestamp = dateToUnixTime(dateStr, endTimeStr) + 86400L;
        Sleep[] sleeps = sleepDao.getSpecificSleep(startTimestamp, endTimestamp);
        Sleep sleep = sleeps[0];

        assertThat(sleep.startTime, equalTo(startTimestamp));
        assertThat(sleep.endTime, equalTo(endTimestamp));
    }

    //TODO: Test Doesn't Work Because Timer Thread Interferes With Auto-Clicking Stop Button
    @Test
    public void testTimer() {
        //INPUT - start and stop the timer lasting 5 seconds
        onView(withId(R.id.startTimerButton)).perform(click());
        long startTimestamp = General.getUnixTime();
        onView(withId(R.id.endTimerButton)).perform(click());
        long endTimestamp = General.getUnixTime();

        Sleep[] sleeps = sleepDao.getSpecificSleep(startTimestamp, endTimestamp);
        Sleep sleep = sleeps[0];

        assertThat(sleep.startTime, equalTo(startTimestamp));
        assertThat(sleep.endTime, equalTo(endTimestamp));
    }
}
