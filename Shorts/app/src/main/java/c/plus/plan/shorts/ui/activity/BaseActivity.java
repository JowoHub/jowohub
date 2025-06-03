package c.plus.plan.shorts.ui.activity;

import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ShortsApplication;
import c.plus.plan.shorts.ad.AdManager;

public class BaseActivity extends AppCompatActivity {
    private ViewModelProvider mActivityProvider;
    private ViewModelProvider mApplicationProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenUtils.setPortrait(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(this);
        }
        return mActivityProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = new ViewModelProvider((ShortsApplication) this.getApplicationContext());
        }
        return mApplicationProvider.get(modelClass);
    }

    public void showToast(int res){
        showToast(getResources().getString(res));
    }

    public void showToast(String toast){
        ToastUtils.make()
                .setBgResource(R.drawable.bg_toast)
                .setTextColor(getResources().getColor(R.color.white))
                .setTextSize(16)
                .setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(100))
                .show(toast);
    }
}
