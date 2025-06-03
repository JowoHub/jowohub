package c.plus.plan.common.entity;

import android.text.TextUtils;

import c.plus.plan.common.constants.KVConstants;
import c.plus.plan.common.utils.KVUtils;

import java.util.UUID;

/**
 * Created by fanwei on 1/23/22
 */
public class Current {
    private static String token;
    private static long uid = -1;
    public static User user;
    public static String uuid;
    private static boolean isLogin = false;

    public static boolean isLogin() {
        return KVUtils.decodeBool(KVConstants.USER_IS_LOGIN);
    }

    public static void setIsLogin(boolean isLogin) {
        Current.isLogin = isLogin;
        KVUtils.encode(KVConstants.USER_IS_LOGIN, isLogin);
    }

    public static String getToken() {
        if(token == null){
            token = KVUtils.decodeString(KVConstants.USER_TOKEN);
        }
        return token;
    }

    public static int getCoin(){
        return user == null ? 0 : user.getCoin();
    }

    public static void setToken(String token) {
        Current.token = token;
        KVUtils.encode(KVConstants.USER_TOKEN, token);
    }

    public static long getUid() {
        if(uid <= 0){
            uid = KVUtils.decodeLong(KVConstants.USER_ID);
        }
        return uid;
    }

    public static void setUid(long uid) {
        Current.uid = uid;
        KVUtils.encode(KVConstants.USER_ID, uid);
    }

    public static String getUuid(){
        if(uuid == null){
            uuid = KVUtils.decodeString(KVConstants.UUID);

            if(TextUtils.isEmpty(uuid)){
                uuid = UUID.randomUUID().toString();
                KVUtils.encode(KVConstants.UUID, uuid);
            }
        }

        return uuid;
    }
}
