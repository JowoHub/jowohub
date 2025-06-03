package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ActivityShopBinding;
import c.plus.plan.shorts.ui.fragment.VipFragment;

public class ShopActivity extends BaseActivity {
    private ActivityShopBinding mBinding;
    private VipFragment vipFragment;

    public static void start(Context context){
        Intent intent = new Intent(context, ShopActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if(savedInstanceState == null){
            vipFragment = new VipFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl, vipFragment)
                    .commit();
            vipFragment.setOnClickListener(new VipFragment.OnClickListener() {
                @Override
                public void vip(boolean suc) {
                    // TODO
                    if(suc){
                        showToast(R.string.vip_sub_suc);
                    }
                }

                @Override
                public void topUp() {

                }
            });
        }

        mBinding.back.setOnClickListener(v -> finish());
    }
}
