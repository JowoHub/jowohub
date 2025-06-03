package c.plus.plan.shorts.ui.fragment;

import static c.plus.plan.common.service.impl.UserServiceImpl.useLanguage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;

import c.plus.plan.common.entity.Current;
import c.plus.plan.common.ui.viewmodel.UserViewModel;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ad.RewardVideoAutoAd;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.FragmentEpisodesBinding;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.adapter.EpisodesAdapter;
import c.plus.plan.shorts.ui.view.CoinUnlockDialog;
import c.plus.plan.shorts.ui.view.GridSpacingItemDecoration;
import c.plus.plan.shorts.ui.view.UnlockDialog;
import c.plus.plan.shorts.ui.viewmodel.EpisodesViewModel;

public class EpisodesFragment extends BaseFragment {
    private FragmentEpisodesBinding mBinding;
    private int mPage;
    private EpisodesAdapter mAdapter;
    private EpisodesViewModel mViewModel;
    private UserViewModel mUserViewModel;
    private DramaPrice mDramaPrice;

    public static EpisodesFragment newInstance(int page) {
        EpisodesFragment fragment = new EpisodesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RouterHub.EXTRA_PAGE, page);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentEpisodesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPage = getArguments().getInt(RouterHub.EXTRA_PAGE);
        mViewModel = getActivityScopeViewModel(EpisodesViewModel.class);
        mUserViewModel = getActivityScopeViewModel(UserViewModel.class);
        mViewModel.getDramaPrice().observe(getViewLifecycleOwner(), result -> {
            mDramaPrice = result;
        });

        initViews();
    }

    private void initViews() {
        mAdapter = new EpisodesAdapter();
        if (mViewModel.getUserUnlockEpisodes() != null) {
            int pageCount = mViewModel.getUserUnlockEpisodes().getDrama().getEpisodesCount() / EpisodesAdapter.PAGE_COUNT;
            if (mViewModel.getUserUnlockEpisodes().getDrama().getEpisodesCount() % EpisodesAdapter.PAGE_COUNT != 0) {
                pageCount++;
            }
            Drama drama = mViewModel.getUserUnlockEpisodes().getDrama();
            if (pageCount == mPage) {
                mAdapter.setEpisodeCount(drama.getEpisodesCount() - (mPage - 1) * EpisodesAdapter.PAGE_COUNT);
            } else {
                mAdapter.setEpisodeCount(EpisodesAdapter.PAGE_COUNT);
            }

            mAdapter.setFreeCount(drama.getFreeEpisodesCount());
            mAdapter.setUnlockNums(mViewModel.getUserUnlockEpisodes().getUnlockEpisodesNum());
            mAdapter.setPage(mPage);
            mAdapter.setCurrentNum(mViewModel.getCurrentNum());

            mAdapter.setOnItemClickListener(new EpisodesAdapter.OnItemClickListener() {
                @Override
                public void click(int num, boolean isLock) {
                    if (isLock) {
                        showUnlockDialog(getContext(), mViewModel.getUserUnlockEpisodes().getDrama(), num);
                    } else {
                        getActivity().finish();
                        SDKExt.playDrama(getContext(), mViewModel.getUserUnlockEpisodes().getDrama().getJowoVid(), mViewModel.getUserUnlockEpisodes().getDrama().getCover(), num, 0);
                    }
                }
            });
        }

        int spanCount = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(6)) / SizeUtils.dp2px(70);
        int spacing = ((ScreenUtils.getScreenWidth() - SizeUtils.dp2px(6)) - SizeUtils.dp2px(65) * spanCount) / (spanCount + 1); // 间距
        boolean includeEdge = true; // 是否包括边缘
        mBinding.rv.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mBinding.rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mBinding.rv.setAdapter(mAdapter);
    }

    private void showUnlockDialog(Context context, Drama drama, int episodesNum) {
        if (VipManager.isVip()) {
            getActivity().finish();
            SDKExt.playEpisodes(context, drama.getJowoVid(), episodesNum, useLanguage());
            return;
        }

        int consumeCoin = mDramaPrice.getCoin(drama.getLevel());
        if (mDramaPrice != null && consumeCoin > -1 && Current.getCoin() >= consumeCoin) {
            // 金币解锁
            if (VipManager.isAutoUnlock()) {
                // 自动解锁
                coinUnlock(context, drama, episodesNum);
            } else {
                CoinUnlockDialog dialog = new CoinUnlockDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(RouterHub.EXTRA_DRAMA_COIN, consumeCoin);
                dialog.setArguments(bundle);
                dialog.setOnClickListener(new CoinUnlockDialog.OnClickListener() {
                    @Override
                    public void unlock() {
                        dialog.dismiss();
                        coinUnlock(context, drama, episodesNum);
                    }
                });
//                dialog.setDialogDismissListener(() -> {
////                    int num = episodesNum - 1;
////                    JOWOSdk.playDrama(context, drama.getId(), drama.getCover(), num, 0);
////                    showToast(R.string.unlock_tip);
//                });
                dialog.show(context);
            }
            return;
        }

        UnlockDialog dialog = new UnlockDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(RouterHub.EXTRA_DRAMA_ID, drama.getId());
        bundle.putInt(RouterHub.EXTRA_EPISODES_NUM, episodesNum);
        dialog.setArguments(bundle);
        dialog.setOnClickListener(new UnlockDialog.OnClickListener() {
            @Override
            public void unlock(DramaNumUnlock dramaNumUnlock) {
                if (dramaNumUnlock.getAdCount() >= DramaNumUnlock.getUnlockAdCount()) {

                    JOWOSdk.unlockEpisodes(drama.getJowoVid(), episodesNum, new Callback<Episodes>() {
                        @Override
                        public void result(Result<Episodes> result) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    });
                } else {
                    if (RewardVideoAutoAd.isReady(getActivity(), Constants.AD_REWARD_CODE)) {
                        RewardVideoAutoAd.show(getActivity(), Constants.AD_REWARD_CODE, new RewardVideoAutoAd.AutoEventListener() {
                            @Override
                            public void onRewardedVideoAdPlayStart(String placementId) {

                            }

                            @Override
                            public void onRewardedVideoAdPlayEnd(String placementId) {

                            }

                            @Override
                            public void onRewardedVideoAdPlayFailed(String fullErrorInfo, String placementId) {

                            }

                            @Override
                            public void onRewardedVideoAdClosed(String placementId) {

                            }

                            @Override
                            public void onReward(String placementId) {
                                int adCount = dialog.adCountPlus();
                                if (adCount >= DramaNumUnlock.getUnlockAdCount()) {

                                    JOWOSdk.unlockEpisodes(drama.getJowoVid(), episodesNum, new Callback<Episodes>() {
                                        @Override
                                        public void result(Result<Episodes> result) {
                                            dialog.dismiss();
                                            getActivity().finish();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        showToast(R.string.no_reward_ad);
                    }

                }
            }

            @Override
            public void vip(boolean suc) {
                if (suc) {
                    dialog.dismissAllowingStateLoss();
                    SDKExt.playEpisodes(context, drama.getJowoVid(), episodesNum, useLanguage());
                }
            }

            @Override
            public void topUp() {
                dialog.dismissAllowingStateLoss();
                coinUnlock(context, drama, episodesNum);
            }
        });
        dialog.show(context);
    }

    private void coinUnlock(Context context, Drama drama, int episodesNum) {
        int consumeCoin = mDramaPrice.getCoin(drama.getLevel());
        RequestUnlockDrama requestUnlockDrama = new RequestUnlockDrama();
        requestUnlockDrama.setDramaId(drama.getId());
        requestUnlockDrama.setDramaCover(drama.getCover());
        requestUnlockDrama.setDramaName(drama.getName());
        requestUnlockDrama.setDramaLevel(drama.getLevel());
        requestUnlockDrama.setDramaEpisodeNum(episodesNum);
        mBinding.loading.setVisibility(View.VISIBLE);
        mViewModel.postUnlockDrama(requestUnlockDrama).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                mUserViewModel.updateUserCoin(Current.getCoin() - consumeCoin);
                mUserViewModel.requestMyUserInfo();
                JOWOSdk.unlockEpisodes(drama.getJowoVid(), episodesNum, new Callback<Episodes>() {
                    @Override
                    public void result(Result<Episodes> result) {
                        // TODO  回调 & loading, 主页一样处理
                        getActivity().finish();
                    }
                });
//                ViewUtils.runOnUiThreadDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(isAdded()){
//                            // TODO  回调 & loading, 主页一样处理
//                            getActivity().finish();
//                        }
//                    }
//                }, 1000);
            } else {
                showToast(R.string.unlock_episodes_fail);
            }
        });
    }
}
