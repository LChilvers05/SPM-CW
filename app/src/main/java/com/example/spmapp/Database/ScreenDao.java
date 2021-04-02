package com.example.spmapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;

@Dao
public interface ScreenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertScreen(Screen screen);
    @Update
    public void updateScreen(Screen screen); // use this to update a session's data
    @Delete
    public void deleteScreen(Screen screen); // use this to delete a single screen
    // we could store a Sleep object in this class which would represent the latest sleep session
    // for which we have identified the preceding screen sessions to make it easier to determine
    // from what date and time on we shold be considering screen sessions as preceding a sleep
    // session
    //Sleep s = new Sleep();
    @Query("SELECT endTime FROM Screen WHERE startTime = :sTime")
    public long getScreenEndTime(long sTime);
    @Query("SELECT * FROM Screen")
    public Screen[] getAllScreens();
    // this assumes that we can compare times; true if they are stored as UNIX timestamps
    //@Query("SELECT screenID FROM Screen WHERE startTime < (SELECT startTime FROM Sleep WHERE sleepID" +
           // " = :slpID" + ") AND startTime > s.endTime")
    //public int[] getCorrespondingScreenSessions(int slpID);
    //tmp: used for JUnit tests
    @Query("SELECT * FROM Screen WHERE startTime = :sTime AND endTime = :eTime")
    Screen[] getSpecificScreen(long sTime, long eTime);

    @Query("SELECT * FROM Screen WHERE endTime >= :sTime AND endTime <= :eTime")
    Screen[] getScreenWithEndBetween(long sTime, long eTime);
    // returns any screens that end between the given times

    @Query("SELECT * FROM Screen WHERE startTime >= :sTime AND startTime <= :eTime")
    Screen[] getScreenWithStartBetween(long sTime, long eTime);

    @Query("SELECT startTime FROM Screen WHERE startTime >= :sTime AND endTime <= :eTime")
    long[] getScreenStartsBetween(long sTime, long eTime);

    @Query("SELECT endTime FROM Screen WHERE startTime >= :sTime AND endTime <= :eTime")
    long[] getScreenEndsBetween(long sTime, long eTime);

    @Query("DELETE FROM Screen WHERE startTime <= :givenTime")
    void deleteScreensStartBefore(long givenTime);
    // deletes all screens that start before/on a timestamp

    @Query("DELETE FROM Screen WHERE startTime >= :givenStart AND startTime <= :givenEnd")
    void deleteScreensBetween(long givenStart, long givenEnd);
    // deletes all screens that took place (even partially) between 2 timestamps

    @Query("DELETE FROM Screen WHERE endTime >= :givenTime")
    void deleteScreensEndedAfter(long givenTime);
    // deletes all screens that ended after/on the timestamp
}
