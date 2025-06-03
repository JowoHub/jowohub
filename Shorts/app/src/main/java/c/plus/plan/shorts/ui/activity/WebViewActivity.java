package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebViewBinding mBinding;
    private String url;

    public static void start(Context context, String url){
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(RouterHub.EXTRA_URL, url);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.back.setOnClickListener(v -> finish());

        url = getIntent().getStringExtra(RouterHub.EXTRA_URL);

        mBinding.web.loadUrl(url);
    }
}
