package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;

import c.plus.plan.common.entity.Current;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ActivityWalletBinding;
import c.plus.plan.shorts.manager.VipManager;

public class WalletActivity extends BaseActivity implements View.OnClickListener {
    private ActivityWalletBinding mBinding;

    public static void start(Context context){
        Intent intent = new Intent(context, WalletActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {
        mBinding.coin.setText(String.valueOf(Current.getCoin()));
        if(VipManager.isVip()){
            mBinding.vip.setVisibility(View.VISIBLE);
            mBinding.vipTime.setText(getResources().getString(R.string.vip_expire_at) + TimeUtils.millis2String(VipManager.getVipTime(), "yyyy-MM-dd"));
        } else {
            mBinding.vip.setVisibility(View.GONE);
        }
        mBinding.sv.setChecked(VipManager.isAutoUnlock());
    }

    private void initViews() {
        mBinding.back.setOnClickListener(this);
        mBinding.topUp.setOnClickListener(this);
        mBinding.record.setOnClickListener(this);
        mBinding.consume.setOnClickListener(this);
        mBinding.vip.setOnClickListener(this);

        mBinding.sv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                VipManager.setAutoUnlock(b);
                if(b){
                    showToast(R.string.auto_unlock_on);
                } else {
                    showToast(R.string.auto_unlock_off);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.back){
            finish();
        } else if(id == R.id.top_up){
            ShopActivity.start(this);
        } else if(id == R.id.record){
            TopUpRecordActivity.start(this);
        } else if(id == R.id.consume){
            ConsumeRecordActivity.start(this);
        } else if(id == R.id.vip){
            VipActivity.start(this);
        }
    }
}
