package com.example.spmapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalTime;

@Database(version = 1, entities = {Sleep.class, Screen.class})
@TypeConverters(DateConverter.class)
abstract class mainDatabase extends RoomDatabase {
    abstract public SleepDao sleepDao();
    abstract public ScreenDao screenDao();
}
