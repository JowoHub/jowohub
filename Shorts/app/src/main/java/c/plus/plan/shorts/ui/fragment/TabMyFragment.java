package c.plus.plan.shorts.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import c.plus.plan.common.entity.Current;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.FragmentTabMyBinding;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.ui.activity.SettingsActivity;
import c.plus.plan.shorts.ui.activity.ShopActivity;
import c.plus.plan.shorts.ui.activity.WalletActivity;
import c.plus.plan.shorts.ui.activity.WebViewActivity;

public class TabMyFragment extends Fragment implements View.OnClickListener {
    private FragmentTabMyBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabMyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews();
    }

    private void updateViews() {
        mBinding.coin.setText(String.valueOf(Current.getCoin()));
        if(VipManager.isVip()){
            mBinding.vip.setVisibility(View.VISIBLE);
        } else {
            mBinding.vip.setVisibility(View.INVISIBLE);
        }

        if(Current.user != null){
            Glide.with(getContext()).load(Current.user.getAvatar()).into(mBinding.avatar);
            mBinding.nickname.setText(Current.user.getNickname());
            mBinding.userId.setText("ID: " + Current.getUid());
        }

    }

    private void initViews() {
        mBinding.mySub.setOnClickListener(this);
        mBinding.policy.setOnClickListener(this);
        mBinding.user.setOnClickListener(this);
        mBinding.topUp.setOnClickListener(this);
        mBinding.myInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.my_sub){
            Uri subscriptionCenterUri = Uri.parse("https://play.google.com/store/account/subscriptions");

            // 创建 Intent 并设置 Action 和 Uri
            Intent intent = new Intent(Intent.ACTION_VIEW, subscriptionCenterUri);

            // 检查是否有应用可以处理该 Intent
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {

            }
        } else if(id == R.id.policy){
            WebViewActivity.start(getContext(), "https://fapp-api.jowo.tv/privacy.html");
        } else if(id == R.id.user){
            WebViewActivity.start(getContext(), "https://fapp-api.jowo.tv/agreement.html");
        } else if(id == R.id.top_up){
            ShopActivity.start(getContext());
        } else if(id == R.id.my_info){
            WalletActivity.start(getContext());
        }
    }
}
