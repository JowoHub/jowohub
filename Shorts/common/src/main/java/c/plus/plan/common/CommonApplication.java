package c.plus.plan.common;

import android.app.Application;

/**
 * Created by fanwei on 1/19/22
 */
public class CommonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        if (NativeManager.get().checkAuth() == 0) {
//            Process.killProcess(Process.myPid());
//        }
    }
}
