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
    public int getSleepStartQuality(long sTime);
    // gets the quality of a sleep given the start timestamp

    @Query("SELECT * FROM Sleep")
    public Sleep[] getAllSleeps();

    //tmp: used for JUnit tests
    @Query("SELECT * FROM Sleep WHERE startTime = :sTime AND endTime = :eTime")
    Sleep[] getSpecificSleep(long sTime, long eTime);

    @Query("SELECT * FROM Sleep WHERE endTime >= :sTime AND endTime <= :eTime")
    Sleep[] getSleepWithEndBetween(long sTime, long eTime);
    // returns any sleeps that end between the given times

    @Query("SELECT * FROM Sleep WHERE startTime >= :sTime AND startTime <= :eTime")
    Sleep[] getSleepWithStartBetween(long sTime, long eTime);
    //returns any sleeps that start between the given times

    @Query("SELECT startTime FROM Sleep WHERE startTime >= :sTime AND endTime <= :eTime")
    long[] getSleepStartsBetween(long sTime, long eTime);

    @Query("SELECT endTime FROM Sleep WHERE startTime >= :sTime AND endTime <= :eTime")
    long[] getSleepEndsBetween(long sTime, long eTime);

    @Query("DELETE FROM Sleep WHERE startTime <= :givenTime")
    void deleteSleepsStartBefore(long givenTime);
    // deletes all sleeps that start before/on a timestamp

    @Query("DELETE FROM Sleep WHERE startTime >= :givenStart AND startTime <= :givenEnd")
    void deleteSleepsBetween(long givenStart, long givenEnd);
    // deletes all sleeps that took place (even partially) between 2 timestamps

    @Query("DELETE FROM Sleep WHERE endTime >= :givenTime")
    void deleteSleepsEndedAfter(long givenTime);
    // deletes all sleeps that ended after/on the timestamp

    @Query("DELETE FROM Sleep")
    void deleteAllSleeps();
    // deletes everything in the sleep table
}
