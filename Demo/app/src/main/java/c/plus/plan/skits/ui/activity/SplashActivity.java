package c.plus.plan.skits.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.skits.databinding.ActivityMainBinding;
import c.plus.plan.skits.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding mBinding;
    private int timerCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if(JOWOSdk.isInitSuccess()){
            toMain();
        } else {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(timerCount >= 11){
                        timer.cancel();
                        ToastUtils.showLong("初始化失败，请检查网络设置后重启APP重试");
                    }
                    if(JOWOSdk.isInitSuccess()){
                        timer.cancel();
                        toMain();
                    }
                    timerCount ++;
                }
            };
            timer.schedule(task, 0, 1000);
        }
    }

    private void toMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
