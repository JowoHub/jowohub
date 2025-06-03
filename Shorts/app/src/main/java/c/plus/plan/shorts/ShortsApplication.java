package c.plus.plan.shorts;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;

import c.plus.plan.common.NativeManager;
import c.plus.plan.common.base.BaseApplication;
import c.plus.plan.common.entity.Current;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.ad.AdManager;

public class ShortsApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("JOWO_APP_ID:"+NativeManager.get().getEncryptByKey(NativeManager.JOWO_APP_ID));

        init();
    }

    @Override
    public void init() {
        super.init();

        initJowo();
        AdManager.get().init(this);

    }

    private void initJowo() {
        JOWOSdk.Config config;
        if(Current.getUid() > 0){
            config = new JOWOSdk.Config.Builder()
                    .setAppId(NativeManager.get().getEncryptByKey(NativeManager.JOWO_APP_ID))
                    .setAesKey(NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_KEY))
                    .setAesIv(NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_IV))
                    .setDebug(AppUtils.isAppDebug())
                    .setLanguage("en")
                    .setUserId(String.valueOf(Current.getUid()))
                    .build();
        } else {
            config = new JOWOSdk.Config.Builder()
                    .setAppId(NativeManager.get().getEncryptByKey(NativeManager.JOWO_APP_ID))
                    .setAesKey(NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_KEY))
                    .setAesIv(NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_IV))
                    .setDebug(AppUtils.isAppDebug())
                    .setLanguage("en")
                    .build();
        }

        LogUtils.d("JOWO_APP_ID:"+NativeManager.get().getEncryptByKey(NativeManager.JOWO_APP_ID));
        LogUtils.d("JOWO_AES_KEY:"+NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_KEY));
        LogUtils.d("JOWO_AES_IV:"+NativeManager.get().getEncryptByKey(NativeManager.JOWO_AES_IV));
        LogUtils.d("isAppDebug:"+AppUtils.isAppDebug());
        LogUtils.d("UserId:"+String.valueOf(Current.getUid()));

        JOWOSdk.init(this, config, result -> {
            if(result != null && result.getCode() == Result.Code.OK){
                // 加载block列表
                JOWOSdk.fetchBlocks(blocks -> {});
            }
        });

    }
}
