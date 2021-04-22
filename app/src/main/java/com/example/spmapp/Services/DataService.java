package com.example.spmapp.Services;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.example.spmapp.Database.MainDatabase;
import com.example.spmapp.Database.ScreenDao;
import com.example.spmapp.Database.SleepDao;
import com.example.spmapp.Helpers.Constants;
import com.example.spmapp.Models.Screen;
import com.example.spmapp.Models.Sleep;

import java.util.Map;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

/**
 * Singleton for communicating with Database, Google Fit and Usage Statistics
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
public class DataService {

    private static DataService INSTANCE;

    private final SleepDao sleepDao;
    private final ScreenDao screenDao;

    private DataService(Application application) {
        this.sleepDao = MainDatabase.getDB(application).sleepDao();
        this.screenDao = MainDatabase.getDB(application).screenDao();

        if (sleepDao.getAllSleeps().length == 0) {
            insertPresentationData();
        };
    }
    //created once in SPMApp Class
    public static void create(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new DataService(application);
        }
    }
    //get single instance
    public static DataService shared() {
        return INSTANCE;
    }

    public void insertPresentationData() {
        //Sprint Review Fri 23rd April
        //Thu 15 - Fri 16
        sleepDao.insertSleep(new Sleep(1618526271, 1618579971, 5));
        screenDao.insertScreen(new Screen(1618508271, 1618522671));
        //Fri 16 - Sat 17
        sleepDao.insertSleep(new Sleep(1618596720, 1618639680, 5));
        screenDao.insertScreen(new Screen(1618579200, 1618586280));
        //Sat 17 - Sun 18
        sleepDao.insertSleep(new Sleep(1618698720, 1618741920, 5));
        screenDao.insertScreen(new Screen(1618687200, 1618698000));
        //Sun 18 - Mon 19
        sleepDao.insertSleep(new Sleep(1618780140, 1618816800, 5));
        screenDao.insertScreen(new Screen(1618751520, 1618754940));
        //Mon 19 - Tue 20
        sleepDao.insertSleep(new Sleep(1618866120, 1618891920, 5));
        screenDao.insertScreen(new Screen(1618837320, 1618862520));
        //Tue 20 - Wed 21
        sleepDao.insertSleep(new Sleep(1618958460, 1619013720, 5));
        screenDao.insertScreen(new Screen(1618939620, 1618957620));
        //Wed 21 - Thu 22
        sleepDao.insertSleep(new Sleep(1619041500, 1619091000, 5));
        screenDao.insertScreen(new Screen(1619030820, 1619037900));
        //Thu 22 - Fri 23
        sleepDao.insertSleep(new Sleep(1619129880, 1619173200, 5));
        screenDao.insertScreen(new Screen(1619120580, 1619128020));
    }

    //DATABASE
    //insert
    public void recordSleepPeriod(long startTime, long endTime, int quality) {
        new Thread(() -> sleepDao.insertSleep(new Sleep(startTime, endTime, quality))).start();
    }

    public void recordScreenPeriod(long startTime, long endTime) {
        new Thread(() -> screenDao.insertScreen(new Screen(startTime, endTime))).start();
    }

    //fetch
    public Sleep[] getSleepsBetweenPeriod(long startTime, long endTime) {
        return sleepDao.getSleepWithStartBetween(startTime, endTime);
    }

    public Screen[] getScreensBetweenPeriod(long startTime, long endTime) {
        return screenDao.getScreenWithEndBetween(startTime, endTime);
    }

    //delete
    public void deleteSleepPeriod(long startTime, long endTime) {
        sleepDao.deleteSleepsBetween(startTime, endTime);
    }
    public void deleteScreenPeriod(long startTime, long endTime) {
        screenDao.deleteScreensBetween(startTime, endTime);
    }


    //USAGE STATISTICS
    //permission
    private boolean checkForUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
        //start settings activity if false
//        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
    //fetch
    public void insertAutomaticScreens(Context context, long startTime, long endTime) {
        if (checkForUsageStatsPermission(context)) {
            UsageStatsManager usageStatsManager
                    = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Map<String, UsageStats> stats
                    = usageStatsManager.queryAndAggregateUsageStats(startTime*1000, endTime*1000);
            stats.entrySet().forEach(entry->{
                long screenEnd = entry.getValue().getLastTimeVisible()/1000L;
                if (screenEnd != 0L) {
                    String[] screenPackage = entry.getValue().getPackageName().split("\\.");
                    String screenName = screenPackage[screenPackage.length - 1];
                    if(!screenName.equals("nexuslauncher")) {
                        long screenDuration = entry.getValue().getTotalTimeVisible()/1000L;
                        recordScreenPeriod(screenEnd - screenDuration, screenEnd);
                    }
                }
            });
        } else {
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }


    public Screen getScreenTimeClosestToSleep(Context context, long sleepStart) {
        Screen[] screens
                = screenDao.getScreenWithEndBetween(sleepStart - 24*Constants.HOUR, sleepStart);

        //gets the screen time closest to sleep by comparing end times
        Screen closest = null;
        for(Screen screen : screens){
            if (closest == null) { closest = screen; }
            if ((screen.endTime > closest.endTime && screen.endTime < sleepStart)) {
                closest = screen;
            }
        }
        return closest;
    }
}
