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
    // we could store a Sleep object in this class which would represent the latest sleep session
    // for which we have identified the preceding screen sessions to make it easier to determine
    // from what date and time on we shold be considering screen sessions as preceding a sleep
    // session
    Sleep s = new Sleep();
    @Query("SELECT durationMins FROM Screen WHERE startDate = :sDate AND startTime = :sTime")
    public int screenDateInfo(Date sDate, Time sTime);
    @Query("SELECT * FROM Sleep")
    public Screen[] getAllScreens();
    // this assumes that we can compare times; true if they are stored as UNIX timestamps
    @Query("SELECT screenID FROM Screen WHERE startTime < (SELECT startDate FROM Sleep WHERE sleepID" +
            " = :slpID" + ") AND startTime > s.endTime")
    static public int[] getCorrespondingScreenSessions(int slpID);
}
