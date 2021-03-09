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
public class Sleep {
    @PrimaryKey(autoGenerate = true)
    public final int sleepID = 0;
    // does it really make sense to set this annotation if these values will be retrieved
    // automatically by the program? they cannot be null anyway
    @NonNull
    public final long startTime;
    @NonNull
    public final long endTime;
    // perhaps we should actually store the endTime information so that we do not have to
    // calculate it afterwards to present it to the user on a graph or in a table?
    // it is also helpful in determining the screen sessions which correspond to a sleep sessions
    // (i.e. are in between the end of the last screen session and the start of this one
    @NonNull
    public final int quality;

    public Sleep(long startTime, long endTime, int quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        // since quality is entered manually, need to validate its value (range 1-10)
        this.quality = quality;
    }
}
