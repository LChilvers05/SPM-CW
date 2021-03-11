package com.example.spmapp.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.spmapp.DateConverter;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;

@Database(version = 1, entities = {Sleep.class, Screen.class})
@TypeConverters(DateConverter.class)
public abstract class MainDatabase extends RoomDatabase {
    //SQLite file
    private static final String DB_NAME = "main_db";
    //is a singleton
    private static MainDatabase instance;
    //getting the one single instance of the DB
    public static synchronized MainDatabase getDB(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MainDatabase.class, DB_NAME).build();
        }
        return instance;
    }

    abstract public SleepDao sleepDao();
    abstract public ScreenDao screenDao();
}
