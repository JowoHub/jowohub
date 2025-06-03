package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStore;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.UserUnlockEpisodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityDramaEpisodesBinding;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.ui.adapter.EpisodesAdapter;
import c.plus.plan.shorts.ui.adapter.EpisodesViewPagerAdapter;
import c.plus.plan.shorts.ui.viewmodel.EpisodesViewModel;

public class DramaEpisodesActivity extends BaseActivity{
    private ActivityDramaEpisodesBinding mBinding;
    private String mDramaId;
    private int mEpisodesNum;
    private EpisodesViewModel mViewModel;
    private EpisodesViewPagerAdapter mViewPager;
    private TabLayoutMediator mMediator;

    public static void start(Context context, String dramaId, int episodesNum){
        Intent intent = new Intent(context, DramaEpisodesActivity.class);
        intent.putExtra(RouterHub.EXTRA_DRAMA_ID, dramaId);
        intent.putExtra(RouterHub.EXTRA_EPISODES_NUM, episodesNum);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void start(Context context, String dramaId, int episodesNum,String cover,long progressTimeMillis){
        Intent intent = new Intent(context, DramaEpisodesActivity.class);
        intent.putExtra(RouterHub.EXTRA_DRAMA_ID, dramaId);
        intent.putExtra(RouterHub.EXTRA_EPISODES_NUM, episodesNum);
        intent.putExtra(RouterHub.EXTRA_EPISODES_COVER, cover);
        intent.putExtra(RouterHub.EXTRA_EPISODES_PROGRESS, cover);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_top_in, R.anim.out_activity);
        mBinding = ActivityDramaEpisodesBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = getActivityScopeViewModel(EpisodesViewModel.class);
        initParams();
        initViews();
    }

    private void initParams() {
        Intent intent = getIntent();
        if(intent != null){
            mDramaId = intent.getStringExtra(RouterHub.EXTRA_DRAMA_ID);
            mEpisodesNum = intent.getIntExtra(RouterHub.EXTRA_EPISODES_NUM, 0);
        }

        if(mDramaId.isBlank() || mEpisodesNum <= 0){
            finish();
        }
    }

    private void initViews() {
        mViewPager = new EpisodesViewPagerAdapter(this);
        mBinding.pager.setAdapter(mViewPager);

        mViewModel.setCurrentNum(mEpisodesNum);
        mViewModel.getData(mDramaId, result -> {
            if(result.getCode() == Result.Code.OK && result.getData() != null){
                updateView();
            } else {
                // TODO 请求失败
            }
        });

        mBinding.close.setOnClickListener(v -> finish());

        mBinding.rb.setChecked(VipManager.isAutoUnlock());

        mBinding.rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = !VipManager.isAutoUnlock();
                VipManager.setAutoUnlock(b);
                mBinding.rb.setChecked(b);

                if(b){
                    showToast(R.string.auto_unlock_on);
                } else {
                    showToast(R.string.auto_unlock_off);
                }
            }
        });
    }

    private void updateView() {
        int pageCount = mViewModel.getUserUnlockEpisodes().getDrama().getEpisodesCount()/EpisodesAdapter.PAGE_COUNT;
        if(mViewModel.getUserUnlockEpisodes().getDrama().getEpisodesCount()%EpisodesAdapter.PAGE_COUNT != 0){
            pageCount ++;
        }
        mViewPager.setPageCount(pageCount);
        int finalPageCount = pageCount;
        mMediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, false, (tab, position) -> {
            if(position < finalPageCount - 1){
                tab.setText((1 + position * EpisodesAdapter.PAGE_COUNT)  + "-" +  (EpisodesAdapter.PAGE_COUNT * (position + 1)));
            } else {
                tab.setText((1 + position * EpisodesAdapter.PAGE_COUNT) + "-" +  mViewModel.getUserUnlockEpisodes().getDrama().getEpisodesCount());
            }
        });
        mMediator.attach();
        mViewPager.notifyDataSetChanged();
        Drama  item = mViewModel.getUserUnlockEpisodes().getDrama();
        String bannerUrl = item.getBanner();
        if (bannerUrl != null && !bannerUrl.startsWith("https://")) {
            String host = Uri.parse(item.getCover()).getHost();
            LogUtils.d("host:" + host);
            bannerUrl = "https://" + host +"/"+ bannerUrl;
        }
        if (bannerUrl == null) {
            bannerUrl = item.getCover();
        }
        Glide.with(this).load(bannerUrl).into(mBinding.cover);
        mBinding.title.setText(mViewModel.getUserUnlockEpisodes().getDrama().getName());
        mBinding.intro.setText(mViewModel.getUserUnlockEpisodes().getDrama().getIntro());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.out_activity, R.anim.slide_top_bottom);
    }
}
