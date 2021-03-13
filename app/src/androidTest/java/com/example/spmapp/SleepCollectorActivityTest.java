package com.example.spmapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.spmapp.Activities.SleepCollectorActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SleepCollectorActivityTest {

    private String startTimeStr;
    private String endTimeStr;
    private String dateStr;

    @Rule
    //something or other

    @Before
    public void init() {
        startTimeStr = "21:00";
        endTimeStr = "7:32";
        dateStr = "13/3/2021";
    }

    @Test
    public void testLog() {
        //INPUT
        onView(withId(R.id.startTimeEntry))
                .perform(typeText(startTimeStr));
        onView(withId(R.id.endTimeEntry))
                .perform(typeText(endTimeStr));
        onView(withId(R.id.startDateEntry))
                .perform(typeText(dateStr));
        //press log button
        onView(withId(R.id.logDataButton)).perform(click());

        //OUTPUT



    }

    @Test
    public void testTimer() {

    }
}
