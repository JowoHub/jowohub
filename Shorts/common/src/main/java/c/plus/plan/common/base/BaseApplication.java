package c.plus.plan.common.base;

import android.app.Application;
import android.os.Build;
import android.os.Process;
import android.webkit.WebView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;
import c.plus.plan.common.NativeManager;
import c.plus.plan.common.constants.Constants;
import c.plus.plan.common.ui.viewmodel.UserViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import java.sql.Time;
import java.util.TimeZone;

/**
 * Create by fw at 2022/1/25
 */
public class BaseApplication extends Application implements ViewModelStoreOwner {

    private ViewModelStore mAppViewModelStore;
    protected UserViewModel userViewModel;
    private ViewModelProvider mApplicationProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        if (NativeManager.get().checkAuth() == 0) {
            Process.killProcess(Process.myPid());
        }

        MMKV.initialize(this);

        LogUtils.getConfig().setLogSwitch(AppUtils.isAppDebug());

        mAppViewModelStore = new ViewModelStore();

        //Android 9及以上必须设置 webview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = Utils.getApp().getProcessName();
            if (!Utils.getApp().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    public void init() {
        userViewModel = getApplicationScopeViewModel(UserViewModel.class);
        userViewModel.getCurrentUser();
        userViewModel.refreshToken();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }

    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = new ViewModelProvider((BaseApplication) this.getApplicationContext());
        }
        return mApplicationProvider.get(modelClass);
    }
}
