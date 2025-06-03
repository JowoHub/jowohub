package c.plus.plan.shorts.ad;

import android.content.Context;

public class AdManager {
    private static AdManager instance;

    private AdManager() {}

    public static synchronized AdManager get() {
        if (instance == null) {
            instance = new AdManager();
        }
        return instance;
    }

    public void init(Context context) {
        //TODO init your ad sdk here
    }
}
