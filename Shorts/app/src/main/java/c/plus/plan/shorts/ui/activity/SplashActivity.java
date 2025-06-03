package c.plus.plan.shorts.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;

import c.plus.plan.common.entity.Current;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ShortsApplication;
import c.plus.plan.shorts.ad.AdSplash;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private final static long AD_DURATION = 5000;
    private ValueAnimator mAnimator;
    private ActivitySplashBinding mBinding;
    private AdSplash adSplash;
    private boolean canJump = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toMain();
    }

    private void initViews() {
        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        mAnimator.setDuration(AD_DURATION);
        // 添加值动画更新监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 在动画更新时执行此方法，获取动画当前值
                float animatedValue = (float) animation.getAnimatedValue();

                // 在这里可以使用动画的当前值来执行相应操作
                // 例如，将动画值应用于视图的属性
                mBinding.pbLoading.setProgress((int) (animatedValue*100));

                if(animatedValue == 1 && !showAd){
                    canJump = true;
                    checkJowosdk();
                }
            }
        });
        // 启动动画
        mAnimator.start();

        mBinding.noNetwork.retry.setOnClickListener(v -> {
            if(JOWOSdk.isInitSuccess()){
                toMain();
            } else {
                mBinding.noNetwork.getRoot().setVisibility(View.GONE);
                mBinding.pbLoading.setProgress(0);
                mAnimator.start();
                ((ShortsApplication)getApplication()).init();
            }
        });

        adSplash = new AdSplash(this, Constants.AD_SPLASH_CODE);
        adSplash.loadAd();
    }

    private boolean showAd;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void checkJowosdk() {
        if(JOWOSdk.isInitSuccess() && !TextUtils.isEmpty(Current.getToken())){
            toMain();
        } else {
            mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
        }
    }

    private void toMain() {
        if(!canJump) return;
        if(!ActivityUtils.isActivityExistsInStack(MainActivity.class)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adSplash != null) {
            adSplash.onDestroy();
        }
    }
}
