package c.plus.plan.shorts.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.DramaForU;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.jowosdk.sdkui.DramaPlayerUiFragment;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.FragmentTabForuBinding;
import c.plus.plan.shorts.ui.activity.MainActivity;
import c.plus.plan.shorts.ui.adapter.ForuViewPagerAdapter;

public class TabForuFragment extends Fragment {
    private static final String TAG = TabForuFragment.class.getSimpleName();
    private FragmentTabForuBinding mBinding;
    private final List<DramaForU> mOldList = new ArrayList<>();
    private final List<DramaForU> mNewList = new ArrayList<>();
    private boolean firstFetch = true;
    private ForuViewPagerAdapter mViewPagerAdapter;
    private int current;
    private boolean play;
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabForuBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (MainActivity) getActivity();
        initViews();
        mBinding.srl.autoRefresh(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(play && TextUtils.equals(mActivity.getCurrentFragment(), RouterHub.FRAGMENT_TAB_FORU)){
            playCurrent(current);
        }
    }
    private final Listener.OnShowListener onShowListener = new Listener.OnShowListener() {
        @Override
        public void showEpisodesList(Context context, Drama drama, int currentNum) {
            LogUtils.dTag(TAG, "showEpisodesList", drama.getId(), currentNum);
        }

        @Override
        public void showUnlock(Context context, Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "showEpisodesList", drama.getId());
        }

        @Override
        public void showUnLockResult(Context context, int dramaId, int num, boolean success) {
            LogUtils.dTag(TAG, "showUnLockResult");
            // 当前fragment不会回调
            // 全局注册监听才走该回调！！！！，后续不再支持
        }

        @Override
        public void showCollectResult(Context context, boolean success) {
            LogUtils.dTag(TAG, "showCollectResult", success);
        }

        @Override
        public void showCancelCollectResult(Context context, boolean success) {
            LogUtils.dTag(TAG, "showCancelCollectResult", success);
        }

        @Override
        public void showMenu(Context context, View view, Drama drama, HashMap<String,Object> extras) {
            LogUtils.dTag(TAG, "showMenu", drama.getId(), extras);
        }

        @Override
        public void showLastPageUp() {
            LogUtils.dTag(TAG, "showLastPageUp");
            ToastUtils.showLong("最后一集上滑，可添加更多剧集推荐");
        }
    };
    private final Listener.OnPlayerListener onPlayerListener = new Listener.OnPlayerListener() {
        @Override
        public void initOk() {

        }

        @Override
        public void start(Drama drama, Episodes episodes) {

        }

        @Override
        public void pause(Drama drama, Episodes episodes, long currentPosition) {

        }

        @Override
        public void finish(Drama drama, Episodes episodes) {
            mBinding.pager.setCurrentItem(current + 1);
        }

        @Override
        public void error(String id, int num, int code, String msg) {

        }


        @Override
        public void controllerClick(int i) {

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        play= true;
    }

    private void fetchData() {
        JOWOSdk.fetchDramasForU(result -> {
            mBinding.srl.finishRefresh();

            if(result.getCode() == Result.Code.OK){
                if(firstFetch){
                    firstFetch = false;
                    mNewList.clear();
                }
                mOldList.clear();
                mOldList.addAll(mNewList);
                mNewList.addAll(result.getData());
                mBinding.srl.finishLoadMore();

                if(mNewList.isEmpty()){
                    mBinding.noData.getRoot().setVisibility(View.VISIBLE);
                }

                updateAdapter();
            } else {
                mBinding.srl.finishLoadMore();
                if(firstFetch && mNewList.isEmpty()){
                    mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateAdapter() {
        mViewPagerAdapter.setDramaForUList(mNewList);
        if(mOldList.isEmpty()){
            mViewPagerAdapter.notifyDataSetChanged();
        } else {
            mViewPagerAdapter.notifyItemRangeInserted(mOldList.size(), mNewList.size() - mOldList.size());
        }
    }


    private void initViews() {
        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            firstFetch = true;
            mBinding.noData.getRoot().setVisibility(View.GONE);
            mBinding.noNetwork.getRoot().setVisibility(View.GONE);
            fetchData();
        });

        mBinding.srl.setOnLoadMoreListener(refreshLayout -> {
            fetchData();
        });

        mViewPagerAdapter = new ForuViewPagerAdapter(this);
        PlayerUiStyle playerUiStyle = new PlayerUiStyle.Builder()
                .setTopBackgroundColor(getResources().getColor(R.color.black))
                .setBackgroundColor(getResources().getColor(R.color.black))
                .setBufferedColor(Color.parseColor("#6C6C6C"))
                .setUnPlayedColor(Color.parseColor("#525252"))
                .setBackRes(R.drawable.ic_back)
                .setCollect(getResources().getString(R.string.collect))
                .setCollectSelected(getResources().getString(R.string.collect_add))
                .setEpisodes(getResources().getString(R.string.episodes))
                .setHeaderPaddingTop(BarUtils.getStatusBarHeight())
                .setLoadingRes(R.layout._video_loading)
                .setShowBack(false).build();
        mViewPagerAdapter.setStyle(playerUiStyle);
        mBinding.pager.setAdapter(mViewPagerAdapter);
        mViewPagerAdapter.setOnPlayerListener(onPlayerListener);
        mViewPagerAdapter.setOnShowListener(onShowListener);
        mBinding.pager.setOffscreenPageLimit(1);

        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                playCurrent(position);
            }
        });

        mBinding.noNetwork.retry.setOnClickListener(view -> {
            mBinding.srl.autoRefresh(0);
        });
    }

    private void playCurrent(int position) {
        int old = current;
        current = position;
        if(old > 0){
            DramaPlayerUiFragment oldFragment = getFragmentByTag("f" + mViewPagerAdapter.getItemId(old));
            if(oldFragment != null){
                oldFragment.videoPause();
            }
        }

        DramaPlayerUiFragment currentFragment = getFragmentByTag("f" + mViewPagerAdapter.getItemId(position));
        if(currentFragment != null){
            currentFragment.videoPlay();
        }
    }

    private DramaPlayerUiFragment getFragmentByTag(String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        return (DramaPlayerUiFragment) fragmentManager.findFragmentByTag(tag);
    }

    public void playCurrent() {
        playCurrent(current);
    }
}
