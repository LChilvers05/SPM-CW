package com.example.spmapp.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.spmapp.Helpers.General;

public class DataCollectorViewModel extends AndroidViewModel {

    SharedPreferences timerPrefs;

//    SleepRepository
//    ScreenTimeRepository

    public DataCollectorViewModel(@NonNull Application application, Context context) {
        super(application);
        this.timerPrefs = context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE);
    }

    public void recordSleepPeriod(Long startTime, Long endTime) {
//        sleepRepository.insert(startTime, endTime)
    }

    public void recordScreenPeriod(Long startTime, Long endTime) {
//        screenTimeRepository.insert(startTime, endTime)
    }

    public void startTimer() {
        SharedPreferences.Editor prefsEditor = timerPrefs.edit();
        prefsEditor.putLong("timer_start_time", General.getUnixTime());
        prefsEditor.apply();
    }

    public void endTimer(Boolean isSleepMode) {
        Long startTime = getTimerStart();
        if (startTime != 0) {
            Long endTime = General.getUnixTime();

            if (isSleepMode) {
                recordSleepPeriod(startTime, endTime);
            } else {
                recordScreenPeriod(startTime, endTime);
            }
        }
    }

    public Long getTimerStart() {
        return timerPrefs.getLong("timer_start_time", 0);
    }
}
