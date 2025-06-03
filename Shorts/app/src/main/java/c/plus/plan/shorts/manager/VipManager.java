package c.plus.plan.shorts.manager;

import c.plus.plan.common.utils.KVUtils;

public class VipManager {
    private static final String VIP_STATE = "vip.state";
    private static final String VIP_TIME = "vip.time";
    private static final String AUTO_UNLOCK = "auto.unlock";

    public static void setVip(boolean isVip){
        KVUtils.encode(VIP_STATE, isVip);
    }

    public static boolean isVip(){
        return KVUtils.decodeBool(VIP_STATE, false);
    }

    public static void setVipTime(long time){
        KVUtils.encode(VIP_TIME, time);
    }

    public static long getVipTime(){
        return KVUtils.decodeLong(VIP_TIME, 0);
    }

    public static void setAutoUnlock(boolean auto){
        KVUtils.encode(AUTO_UNLOCK, auto);
    }

    public static boolean isAutoUnlock(){
        return KVUtils.decodeBool(AUTO_UNLOCK, false);
    }
}
