import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {@ForeignKey(entity = ScreenSleepMap.class,
                        parentColumns = "sleep", childColumns = "sleepID",
                        onDelete = ForeignKey.CASCADE), @ForeignKey(entity = ScreenSleepMap.class,
                        parentColumns = "screen", childColumns = "screenID",
                        onDelete = ForeignKey.CASCADE)})
public class ScreenSleepMap {
    public final int sleep;
    public final int screen;

    public ScreenSleepMap(int sl, int sc) {
        this.sleep = sl;
        this.screen = sc;
    }
    public ScreenSleepMapping(int slpID) {
        int[] a = ScreenDao.CorrespondingScreenSessions(slpID);
        for(int i : a) {
            ScreenSleepMap.insert(new ScreenSleepMap(slpID, i));
        }
    }
}
// still need to define a map from screen sessions to sleep sessions;
// possibly a many-to-many map, because of naps during the day