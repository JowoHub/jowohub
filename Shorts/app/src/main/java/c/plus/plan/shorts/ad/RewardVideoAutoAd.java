package c.plus.plan.shorts.ad;

import android.app.Activity;

import com.blankj.utilcode.util.LogUtils;

import java.util.HashMap;

public class RewardVideoAutoAd {
    private static final String TAG = AdManager.class.getSimpleName();
    private static final HashMap<String, AdReward> mAdMap = new HashMap<>();
    
    public static void init(Activity activity, String[] placementIds){
        LogUtils.dTag(TAG, "RewardVideoAutoAd init");
        for (String id: placementIds) {
            load(activity, id);
        }
    }

    private static void load(Activity activity, String placementId){
        LogUtils.dTag(TAG, "RewardVideoAutoAd load", placementId);
//        TPReward old = mAdMap.get(placementId);
//        if(old != null){
//            old.onDestroy();
//        }
        AdReward adReward = new AdReward(activity, placementId);
        adReward.loadAd();
        mAdMap.put(placementId, adReward);
    }

    public static boolean isReady(Activity activity, String placementId){
        boolean ready = mAdMap.get(placementId) != null && mAdMap.get(placementId).isReady();
        if(!ready){
            load(activity, placementId);
        }
        return ready;
    }

    public static void show(Activity activity, String placementId, AutoEventListener autoEventListener){
        AdReward adReward = mAdMap.get(placementId);
        if(adReward == null){
            load(activity, placementId);
            return;
        }

        LogUtils.dTag(TAG, "RewardVideoAutoAd show", placementId);
        adReward.showAd();
            }


    public interface AutoLoadListener{
        void onRewardVideoAutoLoaded(String placementId);
        void onRewardVideoAutoLoadFail(String placementId, String errorInfo);
    }

    public interface AutoEventListener{
        void onRewardedVideoAdPlayStart(String placementId);
        void onRewardedVideoAdPlayEnd(String placementId);
        void onRewardedVideoAdPlayFailed(String fullErrorInfo, String placementId);
        void onRewardedVideoAdClosed(String placementId);
        void onReward(String placementId);
    }
}
