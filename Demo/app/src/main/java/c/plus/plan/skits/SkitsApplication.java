package c.plus.plan.skits;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.blankj.utilcode.util.Utils;

import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;

public class SkitsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        JOWOSdk.Config config = new JOWOSdk.Config.Builder()
                .setAppId("JW000001")
                .setAesKey("XXEvnDCQo88fmefW")
                .setAesIv("C2wWQwzjPqB8U6xU")
                .setDebug(true)
                .setUserId("")
                .setAndroidId("")
                .setLanguage("")
                .build();


        // 初始化
        JOWOSdk.init(this, config, result -> {
            if (result != null && result.getCode() == Result.Code.OK) {
                // 初始化成功后再调用其他短剧相关接口
            } else {
                // 初始化失败
                Log.e("App", "message: " + (result != null ? result.getMessage() : "App init failed."));
            }
        });

        // 检查api的host是否真的可以访问,如果无法访问大概率是你的ip所在的区域为中国大陆
        // 该方法必须在init之后调用
        JOWOSdk.isReachable(this, 5000, result -> {
            if (result != null && result.getCode() == Result.Code.OK) {
                // 网络可用
            } else {
                // 网络不可用
                Log.e("App", "message: " + (result != null ? result.getMessage() : "Host can not reachable."));
            }
        });


        // import ANDROID 9 + 必须添加，否则部分手机无法播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = Utils.getApp().getProcessName();
            if (!Utils.getApp().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }
}