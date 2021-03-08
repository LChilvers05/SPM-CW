package com.example.spmapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity (primaryKeys = {"startDate", "startTime"})
@TypeConverters(DateConverter.class)
public class Sleep {
    @NonNull
    public final Date startDate;
    @NonNull
    public final Time startTime;
    public final int durationMins;
    public final int quality;

    public Sleep(Date startDate, Time startTime, int durationMins, int quality) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.durationMins = durationMins;
        this.quality = quality;
    }
}
