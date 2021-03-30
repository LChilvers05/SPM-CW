package com.example.spmapp.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Models.Sleep;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Services.DataService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * For DataCollectorActivity subclasses to communicate with MainDatabase through DataService
 */
public class DataCollectorViewModel extends AndroidViewModel {

    //used to save timer start
    SharedPreferences timerPrefs;
    Boolean forSleep;

    public DataCollectorViewModel(@NonNull Application application, Context context, Boolean forSleep) {
        super(application);
        this.timerPrefs = context.getSharedPreferences(Constants.TIMER_PREFS_KEY, Context.MODE_PRIVATE);
        this.forSleep = forSleep;
    }

    //convert date and times to unix timestamp appropriate for DB
    public void logSleep(String date, String startTime, String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH);
        String startDateStr = date + " " + startTime;
        String endDateStr = date + " " + endTime;
        try {
            long startTimestamp = General.getUnixTimeFromDate(formatter.parse(startDateStr));
            long endTimestamp = General.getUnixTimeFromDate(formatter.parse(endDateStr));
            //add a day if end time is less than start
            if (endTimestamp < startTimestamp) {
                endTimestamp = endTimestamp + 86400L;
            }
            DataService.shared().recordSleepPeriod(startTimestamp, endTimestamp, 5);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void logScreen(String date, String duration) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH);
        try {
            long startTimestamp = General.getUnixTimeFromDate(formatter.parse(date));
            String[] splitDuration = duration.split(":");
            long endTimestamp = startTimestamp + 60L*60L*Long.parseLong(splitDuration[0]) + 60L*Long.parseLong(splitDuration[1]);
            DataService.shared().recordScreenPeriod(startTimestamp, endTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //remember the timestamp when sleep/screen manual timer is started
    public void startTimer() {
        SharedPreferences.Editor prefsEditor = timerPrefs.edit();
        prefsEditor.putLong(getKey(), General.getUnixTime());
        prefsEditor.apply();
    }

    //update database with timestamps when sleep/screen manual timer is stopped
    public void endTimer(Boolean forSleep) {
        long startTime = getTimerStart();
        if (startTime != 0L) {
            long endTime = General.getUnixTime();
            if (forSleep) {
                DataService.shared().recordSleepPeriod(startTime, endTime, 5);
            } else {
                DataService.shared().recordScreenPeriod(startTime, endTime);
            }
            //reset as no timer running
            timerPrefs.edit().remove(getKey()).apply();
        }
    }

    public long getTimerStart() {
        return timerPrefs.getLong(getKey(), 0L);
    }
    //so timer for sleep is separated from screen
    private String getKey() {
        if (forSleep) { return Constants.SLEEP_TIMER_START_KEY; }
        return Constants.SCREEN_TIMER_START_KEY;
    }
}
