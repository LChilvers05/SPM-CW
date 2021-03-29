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
public class Sleep {
//    @PrimaryKey(autoGenerate = true)
//    public final int sleepID = 0;
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

    //public long length; // added a length attribute as it's the simplest way to do it in room

    public Sleep(long startTime, long endTime, int quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
        //this.length = endTime - startTime;
    }
}
