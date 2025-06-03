package c.plus.plan.common.entity;

import static c.plus.plan.common.service.impl.UserServiceImpl.useCountry;
import static c.plus.plan.common.service.impl.UserServiceImpl.useLanguage;

import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;

import java.util.TimeZone;

import c.plus.plan.common.NativeManager;
import c.plus.plan.common.R;
import c.plus.plan.common.constants.Constants;
import c.plus.plan.common.constants.KVConstants;
import c.plus.plan.common.network.ApiManager;
import c.plus.plan.common.utils.AesUtils;
import c.plus.plan.common.utils.KVUtils;

/**
 * Created by fanwei on 1/23/22
 */
public class BaseHeader {
    private static final String TAG = Constants.COMMON_TGA + "BaseHeader";
    private static BaseHeader instance;
    private static String mHeaderStr;
    public static final int DEVICE_TYPE_ANDROID = 1;

    private String appId;

    private String deviceId; //androidId
    private String deviceBrand;//手机品牌
    private Boolean IsMfrChannel; //是否厂商
    private int deviceType;//设备类型-android为；1
    private Integer appVersion; //ap版本号
    private String channel; //渠道
    private String cid;
    private double lat;
    private double lon;
    private String language;
    private String country;
    private int coordinateSystem; //坐标系:1百度坐标系，2-国家测绘局，3-WGS坐标系
    private long utcOffset; //时区便移值
    private String unit;


    private BaseHeader(String appId, String deviceId, String brand, int appVersion, String channel) {
        this.appId = appId;
        this.deviceId = deviceId;
        this.deviceBrand = brand;
        this.appVersion = appVersion;
        this.deviceType = 1;
        this.channel = channel;
        this.language = useLanguage();
        this.country = useCountry();
        this.coordinateSystem = 3;
        this.utcOffset = TimeZone.getDefault().getRawOffset();
        this.unit = KVUtils.decodeString(KVConstants.TEMP_UNIT);
    }

    public static BaseHeader get(){
        if (instance == null) {
            synchronized(ApiManager.class){
                if(instance == null){
                    String appId = NativeManager.get().getEncryptByKey(NativeManager.APP_ID);
                    String deviceId = Current.getUuid();
                    String brand = Build.BRAND;
                    int appVersion = AppUtils.getAppVersionCode();
                    String channel = Utils.getApp().getResources().getString(R.string.channel);
                    instance = new BaseHeader(appId, deviceId, brand, appVersion, channel);
                }
            }
        }
        return instance;
    }

    public void setCityInfo(String cid, double lat, double lon){
        this.cid = cid;
        this.lat = lat;
        this.lon = lon;
        // 重置加密header
        BaseHeader.mHeaderStr = null;
    }


    public String getEncryptStr(){
        if(BaseHeader.mHeaderStr == null){
            Gson gson = new Gson();
            String headerJson = gson.toJson(this);
            LogUtils.dTag(TAG, headerJson);
            try {
                String aesKey = NativeManager.get().getEncryptByKey(NativeManager.AES_KEY);
                String aesIv = NativeManager.get().getEncryptByKey(NativeManager.AES_IV);
                BaseHeader.mHeaderStr = AesUtils.encrypt(headerJson, aesKey, aesIv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return BaseHeader.mHeaderStr;
    }

    public void setTempUnit(String unit) {
        this.unit = unit;
        BaseHeader.mHeaderStr = null;
    }
}
