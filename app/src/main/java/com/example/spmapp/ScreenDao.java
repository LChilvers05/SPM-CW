package com.example.spmapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Dao
public interface ScreenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertScreen(Screen screen);
    @Update
    public void updateScreen(Screen screen);
    @Delete
    public void deleteScreen(Screen screen);

    @Query("SELECT durationMins FROM Screen WHERE startDate = :sDate AND startTime = :sTime")
    public int screenDateInfo(Date sDate, Time sTime);
    @Query("SELECT * FROM Sleep")
    public Screen[] getAllScreens();
}
