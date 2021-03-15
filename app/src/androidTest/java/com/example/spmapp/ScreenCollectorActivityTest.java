package com.example.spmapp;

import android.icu.text.UFormat;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.spmapp.Activities.ScreenCollectorActivity;
import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Models.Screen;

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
public class ScreenCollectorActivityTest {

    private ScreenDao screenDao;

    @Rule
    public ActivityScenarioRule<ScreenCollectorActivity> activityScenarioRule
            = new ActivityScenarioRule<ScreenCollectorActivity>(ScreenCollectorActivity.class);

    @Before
    public void init() {
        this.screenDao = MainDatabase.getDB(ApplicationProvider.getApplicationContext()).screenDao();
    }

    private long dateToUnixTime(String dateStr, String timeStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH);
        String timeStampStr = dateStr + " " + timeStr;
        return General.getUnixTimeFromDate(formatter.parse(timeStampStr));
    }

    @Test
    public void testLog() throws ParseException {
        //INPUT - insert in database with UI
        String duration = "8:32";
        String dateStr = "15/3/2021";
        onView(withId(R.id.startTimeEntry))
                .perform(replaceText(duration));
        onView(withId(R.id.sessionDateEntry))
                .perform(replaceText(dateStr));
        onView(withId(R.id.logDataButton)).perform(click());
        //OUTPUT - fetch from database
        long startTimestamp = dateToUnixTime(dateStr, "0:00");
        long endTimestamp = dateToUnixTime(dateStr, duration);
        Screen[] screens = screenDao.getSpecificScreen(startTimestamp, endTimestamp);
        Screen screen = screens[0];

        assertThat(screen.startTime, equalTo(startTimestamp));
        assertThat(screen.endTime, equalTo(endTimestamp));
    }
}
