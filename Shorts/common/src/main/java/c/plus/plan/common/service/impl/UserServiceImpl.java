package c.plus.plan.common.service.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;

import c.plus.plan.common.R;
import c.plus.plan.common.entity.Current;
import c.plus.plan.common.entity.DataResult;
import c.plus.plan.common.entity.User;
import c.plus.plan.common.entity.request.RequestDeviceInfo;
import c.plus.plan.common.entity.response.UserTokenResponse;
import c.plus.plan.common.event.TokenEvent;
import c.plus.plan.common.manager.CommonDataBase;
import c.plus.plan.common.network.ApiManager;
import c.plus.plan.common.network.HttpUserService;
import c.plus.plan.common.service.UserService;

import androidx.lifecycle.LiveData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import c.plus.plan.common.utils.SingleLiveEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fanwei on 2/14/22
 */
public class UserServiceImpl implements UserService {
    private HttpUserService mHttpUserService;
    private int retryTimes = 0;
    private CommonDataBase mCommonDB;

    public UserServiceImpl() {
        mHttpUserService = (HttpUserService) ApiManager.get().getService(HttpUserService.class);
        mCommonDB = CommonDataBase.getDatabase();
    }


    @Override
    public void refreshToken() {
        if(retryTimes > 3) {
            retryTimes = 0;
            return;
        }
        retryTimes++;
        Call<DataResult<UserTokenResponse>> call;
        if(TextUtils.isEmpty(Current.getToken())){
            call = mHttpUserService.createToken(buildDeviceInfo());
            Current.setToken(null);
            Current.setIsLogin(false);
            Current.user = null;
        } else {
            call = mHttpUserService.refreshToken(buildDeviceInfo());
        }
        long s = System.currentTimeMillis();
        call.enqueue(new Callback<DataResult<UserTokenResponse>>() {
            @Override
            public void onResponse(Call<DataResult<UserTokenResponse>> call, Response<DataResult<UserTokenResponse>> response) {
                if(response.isSuccessful() && response.body().isSuccess() && response.body().getData() != null){
                    EventBus.getDefault().post(new TokenEvent(true));
                    User user = response.body().getData().getUser();
                    if(user != null){
                        Current.setUid(user.getId());
                        CommonDataBase.dbWriteExecutor.execute(() -> {
                            mCommonDB.userDao().insert(user);
                        });
                        Current.user = user;
                    }
                    Current.setToken(response.body().getData().getToken());
                    requestMyUserInfo();
                } else {
                    refreshToken();
                    if(retryTimes > 3){
                        EventBus.getDefault().post(new TokenEvent(false));
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResult<UserTokenResponse>> call, Throwable t) {
                refreshToken();
                if(retryTimes > 3){
                    EventBus.getDefault().post(new TokenEvent(false));
                }
            }
        });
    }

    @Override
    public LiveData<DataResult<User>> requestMyUserInfo() {
        SingleLiveEvent<DataResult<User>> result = new SingleLiveEvent<>();
        Call<DataResult<User>> call = mHttpUserService.getMyUserInfo();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResult<User>> call, Response<DataResult<User>> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                    User user = response.body().getData();
                    if(user != null){
                        Current.setUid(user.getId());
                        CommonDataBase.dbWriteExecutor.execute(() -> {
                            mCommonDB.userDao().insert(user);
                        });
                        Current.user = user;
                    }
                } else {
                    result.setValue(DataResult.generateFailResult());
                }
            }

            @Override
            public void onFailure(Call<DataResult<User>> call, Throwable t) {
                result.setValue(DataResult.generateFailResult());
            }
        });
        return result;
    }

    @Override
    public void getCurrentUser() {
        CommonDataBase.dbWriteExecutor.execute(() -> {
            Current.user = mCommonDB.userDao().selectByUid(Current.getUid());
        });
    }

    @Override
    public LiveData<User> getCurrentUserLV() {
        return mCommonDB.userDao().selectByUidLV(Current.getUid());
    }

    @Override
    public LiveData<User> getUserById(long id) {
        return mCommonDB.userDao().selectByUidLV(id);
    }

    @Override
    public LiveData<DataResult> logout() {
        SingleLiveEvent<DataResult> result = new SingleLiveEvent<>();
        Call<DataResult<UserTokenResponse>> call = mHttpUserService.logout();
        call.enqueue(new Callback<DataResult<UserTokenResponse>>() {
            @Override
            public void onResponse(Call<DataResult<UserTokenResponse>> call, Response<DataResult<UserTokenResponse>> response) {
                if(response.isSuccessful() && response.body().getRetCd() == DataResult.RET_CD_SUCCESS){
                    User user = response.body().getData().getUser();
                    if(user != null){
                        Current.setUid(user.getId());
                        CommonDataBase.dbWriteExecutor.execute(() -> {
                            mCommonDB.userDao().insert(user);
                        });
                    }
                    Current.setToken(response.body().getData().getToken());
                    Current.setIsLogin(false);
                    Current.user = user;

                    DataResult dataResult = new DataResult();
                    dataResult.setRetCd(DataResult.RET_CD_SUCCESS);
                    dataResult.setData(user);
                    result.setValue(dataResult);
                } else {
                    DataResult dataResult = new DataResult();
                    dataResult.setRetCd(DataResult.RET_CD_NETWORK_FAIL);
                    result.setValue(dataResult);
                }
            }

            @Override
            public void onFailure(Call<DataResult<UserTokenResponse>> call, Throwable t) {
                DataResult dataResult = new DataResult();
                dataResult.setRetCd(DataResult.RET_CD_NETWORK_FAIL);
                result.setValue(dataResult);
            }
        });
        return result;
    }

    @Override
    public LiveData<DataResult> logoff() {
        SingleLiveEvent<DataResult> result = new SingleLiveEvent<>();
        Call<DataResult<UserTokenResponse>> call = mHttpUserService.logoff();
        call.enqueue(new Callback<DataResult<UserTokenResponse>>() {
            @Override
            public void onResponse(Call<DataResult<UserTokenResponse>> call, Response<DataResult<UserTokenResponse>> response) {
                if(response.isSuccessful() && response.body().getRetCd() == DataResult.RET_CD_SUCCESS){
                    User user = response.body().getData().getUser();
                    if(user != null){
                        Current.setUid(user.getId());
                        CommonDataBase.dbWriteExecutor.execute(() -> {
                            mCommonDB.userDao().insert(user);
                        });
                    }
                    Current.setToken(response.body().getData().getToken());
                    Current.setIsLogin(false);
                    Current.user = user;

                    DataResult dataResult = new DataResult();
                    dataResult.setRetCd(DataResult.RET_CD_SUCCESS);
                    dataResult.setData(user);
                    result.setValue(dataResult);
                } else {
                    DataResult dataResult = new DataResult();
                    dataResult.setRetCd(DataResult.RET_CD_NETWORK_FAIL);
                    result.setValue(dataResult);
                }
            }

            @Override
            public void onFailure(Call<DataResult<UserTokenResponse>> call, Throwable t) {
                DataResult dataResult = new DataResult();
                dataResult.setRetCd(DataResult.RET_CD_NETWORK_FAIL);
                result.setValue(dataResult);
            }
        });
        return result;
    }

    @Override
    public void updateUserCoin(int count) {
        User user = Current.user;
        if(user != null){
            user.setCoin(count);
            Current.user = user;
            CommonDataBase.dbWriteExecutor.execute(() -> {
                mCommonDB.userDao().insert(user);
            });
        }
    }


    @SuppressLint("MissingPermission")
    private RequestDeviceInfo buildDeviceInfo() {
        RequestDeviceInfo deviceInfo = new RequestDeviceInfo();
        deviceInfo.setBrand(Build.BRAND);
        deviceInfo.setDeviceId(Current.getUuid());
        deviceInfo.setChannel(Utils.getApp().getResources().getString(R.string.channel));
        deviceInfo.setType(RequestDeviceInfo.TYPE_ANDROID);
        deviceInfo.setVersion(AppUtils.getAppVersionCode());
        deviceInfo.setVersionCode(String.valueOf(AppUtils.getAppVersionCode()));
        deviceInfo.setHeight(String.valueOf(ScreenUtils.getScreenHeight()));
        deviceInfo.setWidth(String.valueOf(ScreenUtils.getScreenWidth()));
        deviceInfo.setMfrChannel(false);
        if (PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            deviceInfo.setImei(PhoneUtils.getIMEI());
        }

        if(PermissionUtils.isGranted(Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE)){
            deviceInfo.setMac(DeviceUtils.getMacAddress());
        }

        deviceInfo.setModel(DeviceUtils.getModel());
        deviceInfo.setSdCard(SDCardUtils.isSDCardEnableByEnvironment());
        deviceInfo.setSystemCode(String.valueOf(Build.VERSION.RELEASE));
        deviceInfo.setSystemName("Android");
        deviceInfo.setSystemSdk(String.valueOf(DeviceUtils.getSDKVersionCode()));
        deviceInfo.setProduct(Build.PRODUCT);
        deviceInfo.setManufacturer(DeviceUtils.getManufacturer());
        deviceInfo.setLanguage(useLanguage());
        return deviceInfo;
    }


    public static String defaultLanguage(){
//        Locale.getDefault().toLanguageTag();
        return "en-US";
    }

    public static String useLanguage(){
        // LanguageUtils.getSystemLanguage().toString()
        return "en_US";
    }

    public static String useCountry(){
        // LanguageUtils.getSystemLanguage().getCountry()
        return "US";
    }
}
