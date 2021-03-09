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
public interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSleep(Sleep sleep);
    @Update
    public void updateSleep(Sleep sleep);
    @Delete
    public void deleteSleep(Sleep sleep);

    /*@Query("INSERT INTO Sleep VALUES ()")
    public void insertToSleepAsQuery(Date start, Time start, int duration, quality);*/
    @Query("SELECT quality FROM Sleep WHERE startTime = :sTime")
    public int sleepDateInfo(long sTime);
    @Query("SELECT * FROM Sleep")
    public Sleep[] getAllSleeps();
}
