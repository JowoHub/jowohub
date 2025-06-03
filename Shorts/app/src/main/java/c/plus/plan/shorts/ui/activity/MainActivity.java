package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.common.collect.ImmutableList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.common.entity.Current;
import c.plus.plan.common.ui.viewmodel.UserViewModel;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.entity.Feedback;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ad.AdSplash;
import c.plus.plan.shorts.ad.RewardVideoAutoAd;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityMainBinding;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.request.RequestUnlockDrama;
import c.plus.plan.shorts.event.CoinUnlockEvent;
import c.plus.plan.shorts.manager.DramaEventManager;
import c.plus.plan.shorts.manager.ShortsEventManager;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.adapter.MainViewPagerAdapter;
import c.plus.plan.shorts.ui.entity.Menu;
import c.plus.plan.shorts.ui.fragment.TabForuFragment;
import c.plus.plan.shorts.ui.view.CoinUnlockDialog;
import c.plus.plan.shorts.ui.view.LoadingDialog;
import c.plus.plan.shorts.ui.view.MenuDialog;
import c.plus.plan.shorts.ui.view.UnlockDialog;
import c.plus.plan.shorts.ui.viewmodel.MainViewModel;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;
    private MainViewPagerAdapter mViewPager;
    private List<String> mFragments = new ArrayList<>();
    private TabLayoutMediator mMediator;
    private String currentFragment;
    private boolean showSplashAd;
    private AdSplash adSplash;
    private MainViewModel mViewModel;
    private DramaPrice mDramaPrice;
    private UserViewModel mUserViewModel;
    private String mDramaId;
    private int mNum;
    private int consumeCoin;
    private int mVipDramaId;
    private int mVipNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        EventBus.getDefault().register(this);
        mViewModel = getActivityScopeViewModel(MainViewModel.class);
        mUserViewModel = getActivityScopeViewModel(UserViewModel.class);
        mViewModel.getDramaPrice().observe(this, result -> {
            mDramaPrice = result;
        });

        initViews();
        RewardVideoAutoAd.init(this, new String[]{Constants.AD_REWARD_CODE});
//        JOWOSdk.registerOnShowListener(onShowListener);
//        JOWOSdk.registerOnPlayerListener(onPlayListener);


        AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
                if (showSplashAd) {
                    showSplashAd = false;
                    if(adSplash !=null && adSplash.isReady()){
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onBackground(Activity activity) {
                if (!(activity instanceof SplashActivity)) {
                    showSplashAd = true;
                    if(adSplash == null){
                        adSplash = new AdSplash(MainActivity.this, Constants.AD_SPLASH_CODE);
                    }
                    adSplash.loadAd();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayCount = 0;
    }

    @Override
    protected void onDestroy() {
//        JOWOSdk.unRegisterOnShowListener(onShowListener);
//        JOWOSdk.unRegisterOnPlayerListener(onPlayListener);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private int mPlayCount;
//    private final Listener.OnPlayerListener onPlayListener = new Listener.OnPlayerListener() {
//        @Override
//        public void start(Drama drama, Episodes episodes) {
//            cpAdCheck(episodes.getEpisodesNum());
//
//            DramaEventManager.start(drama, episodes, TextUtils.equals(mFragments.get(current), RouterHub.FRAGMENT_TAB_FORU));
//        }
//
//        @Override
//        public void pause(Drama drama, Episodes episodes, long l) {
//            DramaEventManager.pause(drama, episodes, l);
//        }
//
//        @Override
//        public void finish(Drama drama, Episodes episodes) {
//            DramaEventManager.finish(drama, episodes, TextUtils.equals(mFragments.get(current), RouterHub.FRAGMENT_TAB_FORU));
//        }
//
//        @Override
//        public void error(int dramaId, int num, String msg) {
//            DramaEventManager.error(dramaId, num, msg);
//        }
//
//        @Override
//        public void controllerClick(int action) {
//            DramaEventManager.controllerClick(action);
//        }
//    };

    private int lastPlayDramaNum;

    private void cpAdCheck(int num) {
        if (!TextUtils.equals(currentFragment, RouterHub.FRAGMENT_TAB_FORU) && lastPlayDramaNum != num) {
            lastPlayDramaNum = num;
            mPlayCount++;
        }
        LogUtils.dTag("FWFW", "mPlayCount: " + mPlayCount);

        // TODO
    }

//    private final Listener.OnShowListener onShowListener = new Listener.OnShowListener() {
//        @Override
//        public void showEpisodesList(Context context, Drama drama, int num) {
//            DramaEpisodesActivity.start(context, drama.getId(), num);
//        }
//
//        @Override
//        public void showUnlock(Context context, Drama drama, Episodes episodes) {
//            showUnlockDialog(context, drama, episodes);
//        }
//
//        @Override
//        public void showUnLockResult(Context context, int dramaId, int num, boolean b) {
//            if (b) {
//                DramaEventManager.unlock(dramaId, num);
//            } else {
//                showToast(R.string.unlock_episodes_fail);
//            }
//        }
//
//        @Override
//        public void showCollectResult(Context context, boolean b) {
//            if (b) {
//                showToast(R.string.collect_drama_suc);
//            }
//        }
//
//        @Override
//        public void showCancelCollectResult(Context context, boolean b) {
//            if (b) {
//                showToast(R.string.cancel_collect_drams_suc);
//            }
//        }
//
//        @Override
//        public void showMenu(Context context, View view, Drama drama, HashMap<String, Object> extras) {
//            int num = (int) extras.get("episodes_num");
//            showMenuDialog(context, drama, num);
//        }
//
//        @Override
//        public void showLastPageUp() {
//
//        }
//
//
//    };

//    private void showUnlockDialog(Context context, Drama drama, Episodes episodes) {
//        if (VipManager.isVip()) {
//            if (!(mVipDramaId == drama.getId() && mVipNum == episodes.getEpisodesNum())) {
//                mVipDramaId = drama.getId();
//                mVipNum = episodes.getEpisodesNum();
//                SDKExt.playEpisodes(context, drama.getId(), episodes.getEpisodesNum(), useLanguage());
//            }
//
//            return;
//        }
//
//        if (mDramaPrice != null) {
//            int consumeCoin = mDramaPrice.getCoin(drama.getLevel());
//            LogUtils.dTag("FWFW", consumeCoin, drama.getLevel());
//            if (consumeCoin > -1 && Current.getCoin() >= consumeCoin) {
//                // 金币解锁
//                if (VipManager.isAutoUnlock()) {
//                    // 自动解锁
//                    coinUnlock(context, drama, episodes);
//                } else {
//                    CoinUnlockDialog dialog = new CoinUnlockDialog();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(RouterHub.EXTRA_DRAMA_COIN, consumeCoin);
//                    dialog.setArguments(bundle);
//                    dialog.setOnClickListener(new CoinUnlockDialog.OnClickListener() {
//                        @Override
//                        public void unlock() {
//                            dialog.dismiss();
//                            coinUnlock(context, drama, episodes);
//                        }
//                    });
////                    dialog.setDialogDismissListener(() -> {
//////                        int num = episodes.getEpisodesNum() - 1;
//////                        JOWOSdk.playDrama(context, drama.getId(), drama.getCover(), num, 0);
////                        showToast(R.string.unlock_tip);
////                    });
//                    dialog.show(context);
//                }
//                return;
//            }
//        }
//
//        UnlockDialog dialog = new UnlockDialog();
//        Bundle bundle = new Bundle();
//        bundle.putInt(RouterHub.EXTRA_DRAMA_ID, drama.getId());
//        bundle.putInt(RouterHub.EXTRA_EPISODES_NUM, episodes.getEpisodesNum());
//        dialog.setArguments(bundle);
//        dialog.setOnClickListener(new UnlockDialog.OnClickListener() {
//            @Override
//            public void unlock(DramaNumUnlock dramaNumUnlock) {
//                if (dramaNumUnlock.getAdCount() >= DramaNumUnlock.getUnlockAdCount()) {
//                    JOWOSdk.unlockEpisodes(drama.getId(), episodes.getEpisodesNum(), new Callback<Episodes>() {
//                        @Override
//                        public void result(Result<Episodes> result) {
//                            dialog.dismissAllowingStateLoss();
//                        }
//                    });
//                } else {
//                    if (RewardVideoAutoAd.isReady(MainActivity.this, Constants.AD_REWARD_CODE)) {
//                        RewardVideoAutoAd.show(MainActivity.this, Constants.AD_REWARD_CODE, new RewardVideoAutoAd.AutoEventListener() {
//                            @Override
//                            public void onRewardedVideoAdPlayStart(String placementId) {
//
//                            }
//
//                            @Override
//                            public void onRewardedVideoAdPlayEnd(String placementId) {
//
//                            }
//
//                            @Override
//                            public void onRewardedVideoAdPlayFailed(String fullErrorInfo, String placementId) {
//
//                            }
//
//                            @Override
//                            public void onRewardedVideoAdClosed(String placementId) {
//
//                            }
//
//                            @Override
//                            public void onReward(String placementId) {
//                                ViewUtils.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int adCount = dialog.adCountPlus();
//                                        if (adCount >= DramaNumUnlock.getUnlockAdCount()) {
//                                            JOWOSdk.unlockEpisodes(drama.getId(), episodes.getEpisodesNum(), new Callback<Episodes>() {
//                                                @Override
//                                                public void result(Result<Episodes> result) {
//                                                    dialog.dismissAllowingStateLoss();
//                                                }
//                                            });
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                    } else {
//                        showToast(R.string.no_reward_ad);
//                    }
//
//                }
//            }
//
//            @Override
//            public void vip(boolean suc) {
//                if (suc) {
//                    dialog.dismissAllowingStateLoss();
//                    SDKExt.playEpisodes(context, drama.getId(), episodes.getEpisodesNum(), useLanguage());
//                }
//            }
//
//            @Override
//            public void topUp() {
//                dialog.dismissAllowingStateLoss();
//                coinUnlock(context, drama, episodes);
//            }
//        });
////        dialog.setDialogDismissListener(() -> {
////            int num = episodes.getEpisodesNum() - 1;
////            JOWOSdk.playDrama(context, drama.getId(), drama.getCover(), num, 0);
////            showToast(R.string.unlock_tip);
////        });
//        dialog.show(context);
//    }

    private void coinUnlock(Context context, Drama drama, Episodes episodes) {
        // TODO loading处理
        LoadingDialog.showLoading(context, false);
        RequestUnlockDrama requestUnlockDrama = new RequestUnlockDrama();
        requestUnlockDrama.setDramaId(drama.getId());
        requestUnlockDrama.setDramaCover(drama.getCover());
        requestUnlockDrama.setDramaName(drama.getName());
        requestUnlockDrama.setDramaLevel(drama.getLevel());
        requestUnlockDrama.setDramaEpisodeNum(episodes.getEpisodesNum());
        mDramaId = drama.getJowoVid();
        mNum = episodes.getEpisodesNum();
        consumeCoin = mDramaPrice.getCoin(drama.getLevel());
        mViewModel.postUnlockDrama(requestUnlockDrama).observe(MainActivity.this, result -> {

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CoinUnlockEvent event) {
        if (!mDramaId.isBlank()) {
            LoadingDialog.dismissLoading();
            if (event.success) {
                mUserViewModel.updateUserCoin(Current.getCoin() - consumeCoin);
                mUserViewModel.requestMyUserInfo();
                JOWOSdk.unlockEpisodes(mDramaId, mNum, new Callback<Episodes>() {
                    @Override
                    public void result(Result<Episodes> result) {

                    }
                });
                mDramaId = null;
            } else {
                showToast(R.string.unlock_episodes_fail);
            }
        }
    }


//    private void showMenuDialog(Context context, Drama drama, int num) {
//        MenuDialog dialog = new MenuDialog();
//        Bundle bundle = new Bundle();
//        ArrayList<Menu> menuList = new ArrayList();
//        menuList.add(new Menu(getResources().getString(R.string.report)));
//        bundle.putParcelableArrayList(RouterHub.EXTRA_MENU, menuList);
//        dialog.setArguments(bundle);
//        dialog.setOnClickListener(new MenuDialog.OnClickListener() {
//            @Override
//            public void click(Menu menu) {
//                dialog.dismiss();
//                Feedback feedback = new Feedback();
//                feedback.setDramaId(drama.getId());
//                feedback.setEpisodesNum(num);
//                feedback.setFeedType(Feedback.TYPE_REPORT);
//                FeedbackActivity.start(context, feedback);
//            }
//        });
//        dialog.show(context);
//    }

    private void initViews() {
        mViewPager = new MainViewPagerAdapter(this);
        mFragments.add(RouterHub.FRAGMENT_TAB_HOME);
        mFragments.add(RouterHub.FRAGMENT_TAB_FORU);
        mFragments.add(RouterHub.FRAGMENT_TAB_RECORD);
        mFragments.add(RouterHub.FRAGMENT_TAB_MY);
        mViewPager.setFragmentNames(mFragments);
        mBinding.pager.setAdapter(mViewPager);
        mBinding.pager.setUserInputEnabled(false);

        mBinding.tab.addOnTabSelectedListener(onTabSelectedListener);
        mMediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, false, (tab, position) -> {
            tab.setCustomView(R.layout.item_main_tab);
            updateTabIcon(tab, position, false);
        });
        mMediator.attach();

        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                LogUtils.dTag("FWFW", "onBillingSetupFinished", billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryGoods();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // TODO
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                LogUtils.dTag("FWFW", "onBillingServiceDisconnected");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DramaEventManager.onEvent(TextUtils.equals(mFragments.get(current), RouterHub.FRAGMENT_TAB_FORU));
    }

    private void back() {
        ActivityUtils.startHomeActivity();
    }

    boolean defaultTab = true;
    private int current;
    private final TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            updateTabIcon(tab, tab.getPosition(), true);
            currentFragment = mFragments.get(tab.getPosition());
            if (TextUtils.equals(mFragments.get(tab.getPosition()), RouterHub.FRAGMENT_TAB_FORU)) {
                TabForuFragment currentFragment = (TabForuFragment) getSupportFragmentManager().findFragmentByTag("f" + mViewPager.getItemId(tab.getPosition()));
                if (currentFragment != null) {
                    currentFragment.playCurrent();
                }
            }
            current = tab.getPosition();
            if (defaultTab) {
                defaultTab = false;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("tab", mFragments.get(tab.getPosition()));
                ShortsEventManager.onEvent("tab_click", bundle);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            updateTabIcon(tab, tab.getPosition(), false);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void updateTabIcon(TabLayout.Tab tab, int position, boolean b) {
        mViewPager.updateTabIcon(tab, position, b);
    }

    public CharSequence getCurrentFragment() {
        return currentFragment;
    }

    private BillingClient billingClient;

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (CollectionUtils.isNotEmpty(purchases)) {
                    boolean buyVip = false;
                    for (Purchase purchase : purchases) {
                        if (TextUtils.equals(purchase.getProducts().get(0), Constants.VIP_SUB_PRODUCT_ID)) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                VipManager.setVipTime(purchase.getPurchaseTime());

                                buyVip = true;
                            }
                        }
                        if (!purchase.isAcknowledged()) {
                            AcknowledgePurchaseParams acknowledgePurchaseParams =
                                    AcknowledgePurchaseParams.newBuilder()
                                            .setPurchaseToken(purchase.getPurchaseToken())
                                            .build();
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams, null);
                        }
                    }
                    VipManager.setVip(buyVip);
                } else {
                    VipManager.setVip(false);
                }
            }
        }
    };

    private void queryGoods() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(Constants.VIP_SUB_PRODUCT_ID)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (CollectionUtils.isNotEmpty(purchases)) {
                                VipManager.setVip(true);
                                for (Purchase purchase : purchases) {
                                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                        VipManager.setVipTime(purchase.getPurchaseTime());
                                    }
                                    if (!purchase.isAcknowledged()) {
                                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                                AcknowledgePurchaseParams.newBuilder()
                                                        .setPurchaseToken(purchase.getPurchaseToken())
                                                        .build();
                                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, null);
                                    }
                                }
                            } else {
                                VipManager.setVip(false);
                            }
                        }
                    }
                });
    }


}
