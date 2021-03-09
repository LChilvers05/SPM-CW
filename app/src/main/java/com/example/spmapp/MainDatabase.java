package com.example.spmapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.time.LocalDate;
import java.time.LocalTime;

@Database(version = 1, entities = {Sleep.class, Screen.class})
@TypeConverters(DateConverter.class)
public abstract class MainDatabase extends RoomDatabase {
    abstract public SleepDao sleepDao();
    abstract public ScreenDao screenDao();

    public static MainDatabase getDB(Context context) {
        return Room.databaseBuilder(context, MainDatabase.class, "main-db").build();
    }
}
