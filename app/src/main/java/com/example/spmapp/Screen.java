package com.example.spmapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity//(primaryKeys = {"startDate", "startTime"})
@TypeConverters(DateConverter.class)
public class Screen {
    // since these records are referenced in the ScreenSleepMap, there should probably be a
    // new simple primary key so that it is easier to reference
    @PrimaryKey(autoGenerate = true)
    public final int screenID;
    @NonNull
    public final Date startDate;
    @NonNull
    public final Time startTime;
    public final Time endTime;
    public final int durationMins;

    public Screen(Date startDate, Time startTime, Time endTime, int durationMins) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMins = durationMins;
    }
}
