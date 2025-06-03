package c.plus.plan.common;

import android.content.Context;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Created by fanwei on 1/23/22
 */
public class NativeManager {
    public static final String PRODUCT_HOST = "PRODUCT_HOST";
    public static final String TEST_HOST = "TEST_HOST";
    public static final String APP_ID = "APP_ID";
    public static final String AES_KEY = "AES_KEY";
    public static final String AES_IV = "AES_IV";
    public static final String APP_METRICA_KEY = "APP_METRICA_KEY";
    public static final String JOWO_APP_ID = "JOWO_APP_ID";
    public static final String JOWO_AES_KEY = "JOWO_AES_KEY";
    public static final String JOWO_AES_IV = "JOWO_AES_IV";

    private static NativeManager mInstance;

    public static NativeManager get() {
        if (mInstance == null) {
            synchronized (NativeManager.class) {
                if (mInstance == null) {
                    initNativeLib(Utils.getApp(), false);
                    mInstance = new NativeManager();
                }
            }
        }
        return mInstance;
    }

    private native static void initNativeLib(Context context, boolean isDebug);

    public native String getUrlByKey(String key);

    public native String getEncryptByKey(String key);

    public native int checkAuth();

    static {
        System.loadLibrary("common");
    }
}
