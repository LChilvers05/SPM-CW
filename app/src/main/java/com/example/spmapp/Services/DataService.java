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
        //Sprint Review Mon 12th April
        //Wed 31st - Thu 1st
        sleepDao.insertSleep(new Sleep(1617229845, 1617268365, 5));
        screenDao.insertScreen(new Screen(1617225165, 1617227805));
        //Thu 1st - Fri 2nd
        sleepDao.insertSleep(new Sleep(1617309285, 1617337005, 5));
        screenDao.insertScreen(new Screen(1617296985, 1617304005));
        //Fri 2nd - Sat 3rd
        sleepDao.insertSleep(new Sleep(1617392025, 1617430185, 5));
        screenDao.insertScreen(new Screen(1617376605, 1617391005));
        //Sat 3rd - Sun 4th
        sleepDao.insertSleep(new Sleep(1617481845, 1617534105, 5));
        screenDao.insertScreen(new Screen(1617438645, 1617439965));
        //Sun 4th - Mon 5th
        sleepDao.insertSleep(new Sleep(1617566565, 1617609765, 5));
        screenDao.insertScreen(new Screen(1617544965, 1617559365));
        //Mon 5th - Tue 6th
        sleepDao.insertSleep(new Sleep(1617654616, 1617690616, 5));
        screenDao.insertScreen(new Screen(1617648310, 1617653530));
        //Tue 6th - Wed 7th
        sleepDao.insertSleep(new Sleep(1617743712, 1617772512, 5));
        screenDao.insertScreen(new Screen(1617708850, 1617710350));
        //Wed 7th - Thu 8th
        sleepDao.insertSleep(new Sleep(1617836313, 1617851003, 5));
        screenDao.insertScreen(new Screen(1617823845, 1617823845));
        //Thu 8th - Fri 9th
        sleepDao.insertSleep(new Sleep(1617906236, 1617962462, 5));
        screenDao.insertScreen(new Screen(1617889605, 1617900405));
        //Fri 9th - Sat 10th
        sleepDao.insertSleep(new Sleep(1617999705, 1618040771, 5));
        screenDao.insertScreen(new Screen(1617962482, 1617999002));
        //Sat 10th - Sun 11th
        sleepDao.insertSleep(new Sleep(1618076712, 1618103516, 5));
        screenDao.insertScreen(new Screen(1618060125, 1618063725));
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


    //GOOGLE FIT





}
