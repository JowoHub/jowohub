package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Banner;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.databinding.FragmentTabMainBinding;
import c.plus.plan.skits.ui.activity.PlayerActivity;
import c.plus.plan.skits.ui.adapter.BlockAndDramasAdapter;
import c.plus.plan.skits.ui.adapter.DramaBannerAdapter;


public class TabMainFragment extends Fragment {
    private FragmentTabMainBinding mBinding;
    private List<Banner> mBannerList = new ArrayList<>();
    private BlockAndDramasAdapter mBlockAndDramasAdapter;
    private DramaBannerAdapter mBannerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        mBinding.srl.autoRefresh();
        loadCache();
    }

    private void loadCache() {
        JOWOSdk.lastBanner(result -> {
            if(result.getCode() == Result.Code.OK){
                ViewUtils.runOnUiThread(() -> {
                    mBinding.banner.getAdapter().setDatas(result.getData());
                    mBinding.banner.getAdapter().notifyDataSetChanged();
                });
            } else {
                // TODO 失败处理
            }
        });

        JOWOSdk.lastBlockDrama(result -> {
            if(result.getCode() == Result.Code.OK){
                ViewUtils.runOnUiThread(() -> {
                    mBlockAndDramasAdapter.setBlockAndDramasList(result.getData());
                    mBlockAndDramasAdapter.notifyDataSetChanged();
                });
            } else {
                // TODO 失败处理
            }
        });
    }

    private void initViews() {
        mBannerAdapter = new DramaBannerAdapter(mBannerList);
        ViewGroup.LayoutParams bannerLayoutParams = mBinding.banner.getLayoutParams();
        if(bannerLayoutParams != null){
            bannerLayoutParams.height = (int) (ScreenUtils.getScreenWidth() * 0.5625);
        }
        mBinding.banner.addBannerLifecycleObserver(getViewLifecycleOwner())
                .setAdapter(mBannerAdapter);

        mBlockAndDramasAdapter = new BlockAndDramasAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setLayoutManager(layoutManager);
        mBinding.rv.setAdapter(mBlockAndDramasAdapter);

        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            fetchBannerList();
            fetchBlockAndDramasList();
        });

        mBannerAdapter.setOnItemClickListener(banner -> {
//            JOWOSdk.playDrama(getContext(), banner.getDrama().getId(), banner.getDrama().getCover(), banner.getEpisodesNum(), 0);
            PlayerActivity.start(getContext(), banner.getDrama().getJowoVid(), banner.getEpisodesNum(), banner.getDrama().getCover(), 0);
        });

        mBlockAndDramasAdapter.setOnItemClickListener((position, item) -> {
//            JOWOSdk.playDrama(getContext(), item.getId(), item.getCover(), 0, 0);
            PlayerActivity.start(getContext(), item.getJowoVid(), 0, item.getCover(), 0);
        });
    }

    private void fetchBlockAndDramasList() {
        JOWOSdk.fetchBlockDrama(result -> {
            if(result.getCode() == Result.Code.OK){
                mBlockAndDramasAdapter.setBlockAndDramasList(result.getData());
                mBlockAndDramasAdapter.notifyDataSetChanged();
            } else {
                // TODO 失败处理
            }
        });
    }

    private void fetchBannerList() {
        JOWOSdk.fetchBanner(new Callback<>() {
            @Override
            public void result(Result<List<Banner>> result) {
                if(result.getCode() == Result.Code.OK){
                    mBinding.srl.finishRefresh();
                    mBinding.banner.getAdapter().setDatas(result.getData());
                    mBinding.banner.getAdapter().notifyDataSetChanged();
                } else {
                    // TODO 失败处理
                }

            }
        });
    }
}
