package com.example.spmapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity//(primaryKeys = {"startDate", "startTime"})
@TypeConverters(DateConverter.class)
public class Sleep {
    @PrimaryKey(autoGenerate = true);
    public final int sleepID;
    // does it really make sense to set this annotation if these values will be retrieved
    // automatically by the program? they cannot be null anyway
    @NonNull
    public final Date startDate;
    @NonNull
    public final Time startTime;
    // perhaps we should actually store the endTime information so that we do not have to
    // calculate it afterwards to present it to the user on a graph or in a table?
    // it is also helpful in determining the screen sessions which correspond to a sleep sessions
    // (i.e. are in between the end of the last screen session and the start of this one
    @NonNull
    public final Time endTime;
    public final int durationMins;
    @NonNull
    public final int quality;

    public Sleep(Date startDate, Time startTime, Time endTime, int quality) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMins = durationMins;
        // since quality is entered manually, need to validate its value (range 1-10)
        if(quality > 0 && quality < 11) {
            this.quality = quality;
     }
        else {
            this.quality = 6;
        }
    }
}
