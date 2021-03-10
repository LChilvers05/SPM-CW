package com.example.spmapp.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Helpers.General;
import com.example.spmapp.MainDatabase;
import com.example.spmapp.Screen;
import com.example.spmapp.ScreenDao;
import com.example.spmapp.Sleep;
import com.example.spmapp.SleepDao;

public class DataCollectorViewModel extends AndroidViewModel {

    //used to save timer start
    SharedPreferences timerPrefs;

    SleepDao sleepDao;
    ScreenDao screenDao;

    public DataCollectorViewModel(@NonNull Application application, Context context) {
        super(application);
        this.timerPrefs = context.getSharedPreferences(Constants.TIMER_PREFS_KEY, Context.MODE_PRIVATE);
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();
    }

    //log in DB
    public void recordSleepPeriod(Long startTime, Long endTime) {
        new Thread() {
            public void run() {
                sleepDao.insertSleep(new Sleep(startTime, endTime, 5));
            }
        }.start();
    }

    public void recordScreenPeriod(Long startTime, Long endTime) {
        new Thread() {
            public void run() {
                screenDao.insertScreen(new Screen(startTime, endTime));
            }
        }.start();
    }

    //remember the timestamp when sleep/screen manual timer is started
    public void startTimer() {
        SharedPreferences.Editor prefsEditor = timerPrefs.edit();
        prefsEditor.putLong(Constants.TIMER_START_KEY, General.getUnixTime());
        prefsEditor.apply();
    }

    //update database with timestamps when sleep/screen manual timer is stopped
    public void endTimer(Boolean forSleep) {
        Long startTime = getTimerStart();
        if (startTime != 0) {
            Long endTime = General.getUnixTime();
            if (forSleep) {
                recordSleepPeriod(startTime, endTime);
            } else {
                recordScreenPeriod(startTime, endTime);
            }
            //reset as no timer running
            timerPrefs.edit().remove(Constants.TIMER_START_KEY).apply();
        }
    }

    public Long getTimerStart() {
        return timerPrefs.getLong(Constants.TIMER_START_KEY, 0L);
    }
}
