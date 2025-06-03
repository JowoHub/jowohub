package c.plus.plan.common.utils;

import com.tencent.mmkv.MMKV;

public class KVUtils {
    public static boolean encode(String key, String value){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).encode(key, value);
    }

    public static boolean encode(String key, long value){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).encode(key, value);
    }

    public static boolean encode(String key, boolean value){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).encode(key, value);
    }

    public static String decodeString(String key){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeString(key);
    }

    public static String decodeString(String key, String value){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeString(key, value);
    }

    public static boolean decodeBool(String key){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeBool(key);
    }

    public static boolean decodeBool(String key, boolean defaultV){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeBool(key, defaultV);
    }

    public static long decodeLong(String key){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeLong(key);
    }

    public static long decodeLong(String key, long defaultValue){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeLong(key, defaultValue);
    }

    public static int decodeInt(String key){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeInt(key);
    }

    public static int decodeInt(String key, int defaultValue){
        return MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null).decodeInt(key, defaultValue);
    }
}
