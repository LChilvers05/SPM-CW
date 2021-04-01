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
    //TODO: getLastTimestamp() is an important attribute
    public Map<String, UsageStats> getUsageStatistics(Context context, long startTime, long endTime) {
        if (checkForUsageStatsPermission(context)) {
            UsageStatsManager usageStatsManager
                    = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            Map<String, UsageStats> stats
                    = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
            //TODO: filter the stats to things the user cares about
            //print out the stats
//            stats.entrySet().forEach(entry->{
//                System.out.println(entry.getValue().getPackageName() + " " + entry.getValue().getLastTimeVisible());
//            });
            return stats;
        } else {
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
        return null;
    }


    public Screen getScreenTimeClosestToSleep(Context context, long sleepStart) {
        Screen[] manualScreens
                = screenDao.getScreenWithEndBetween(sleepStart - 8*Constants.HOUR, sleepStart);

        //gets the screen time closest to sleep by comparing end times
        Screen maxMan = null;
        for(Screen screen : manualScreens){
            if (maxMan == null) { maxMan = screen; }
            if ((screen.endTime > maxMan.endTime && screen.endTime < sleepStart)) {
                maxMan = screen;
            }
        }

//        Map<String, UsageStats> automaticScreens
//                = getUsageStatistics(context, sleepStart - 4*Constants.HOUR, sleepStart);

//        Float duration = 0.0F;

        return maxMan;
    }


    //GOOGLE FIT





}
