package com.example.spmapp.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.spmapp.DateConverter;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity(primaryKeys = {"startTime", "endTime"})
@TypeConverters(DateConverter.class)
public class Screen {
    // since these records are referenced in the ScreenSleepMap, there should probably be a
    // new simple primary key so that it is easier to reference
//    @PrimaryKey(autoGenerate = true)
//    public final int screenID = 0;
    @NonNull
    public final long startTime;
    @NonNull
    public final long endTime;

    public Screen(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
