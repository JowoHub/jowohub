package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;

import c.plus.plan.shorts.databinding.ActivitySettingsBinding;

public class SettingsActivity extends BaseActivity{
    private ActivitySettingsBinding mBinding;

    public static void start(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
    }

    private void initViews() {
        mBinding.version.setText(AppUtils.getAppVersionName());

        mBinding.back.setOnClickListener(v -> finish());
        mBinding.user.setOnClickListener(v -> {
            WebViewActivity.start(SettingsActivity.this, "https://fapp-api.jowo.tv/agreement.html");
        });

        mBinding.policy.setOnClickListener(v -> {
            WebViewActivity.start(SettingsActivity.this, "https://fapp-api.jowo.tv/privacy.html");
        });
    }
}
