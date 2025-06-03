package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.bumptech.glide.Glide;

import c.plus.plan.common.entity.Current;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ActivityVipBinding;
import c.plus.plan.shorts.databinding.ActivityWalletBinding;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.ui.view.CustomerDialog;

public class VipActivity extends BaseActivity implements View.OnClickListener {

    private ActivityVipBinding mBinding;

    public static void start(Context context){
        Intent intent = new Intent(context, VipActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVipBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
    }

    private void initViews() {
        mBinding.back.setOnClickListener(this);
        mBinding.vipManage.setOnClickListener(this);
        mBinding.customer.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {
        if(VipManager.isVip()){
            mBinding.vip.setVisibility(View.VISIBLE);
            mBinding.vipTime.setText(getResources().getString(R.string.vip_expire_at) + TimeUtils.millis2String(VipManager.getVipTime(), "yyyy-MM-dd"));
        } else {
            mBinding.vip.setVisibility(View.INVISIBLE);
        }

        if(Current.user != null){
            Glide.with(this).load(Current.user.getAvatar()).into(mBinding.avatar);
            mBinding.nickname.setText(Current.user.getNickname());
            mBinding.userId.setText("ID: " + Current.getUid());
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.back){
            finish();
        } else if(id == R.id.vip_manage){
            Uri subscriptionCenterUri = Uri.parse("https://play.google.com/store/account/subscriptions");

            // 创建 Intent 并设置 Action 和 Uri
            Intent intent = new Intent(Intent.ACTION_VIEW, subscriptionCenterUri);

            // 检查是否有应用可以处理该 Intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {

            }
        } else if(id == R.id.customer){
            // reelmax@163.com
            CustomerDialog dialog = new CustomerDialog();
            dialog.show(this);
        }
    }
}
