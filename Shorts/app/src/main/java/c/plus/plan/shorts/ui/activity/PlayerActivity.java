package c.plus.plan.shorts.ui.activity;

import static c.plus.plan.common.service.impl.UserServiceImpl.useLanguage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ViewUtils;

import java.util.HashMap;
import java.util.Objects;

import c.plus.plan.common.entity.Current;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.EpisodeShareUrl;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.jowosdk.sdkui.DramaEpisodesPlayerUiFragment;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ad.RewardVideoAutoAd;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityPlayerBinding;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.manager.VipManager;
import c.plus.plan.shorts.network.DramaServiceImpl;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.view.CoinUnlockDialog;
import c.plus.plan.shorts.ui.view.LoadingDialog;
import c.plus.plan.shorts.ui.view.UnlockDialog;

public class PlayerActivity extends AppCompatActivity {
    private static final String DRAMA_ID = "id";
    private static final String DRAMA_EPISODES_NUM = "num";
    private static final String DRAMA_COVER = "cover";
    private static final String DRAMA_PLAYER_POSITION = "position";
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private ActivityPlayerBinding mBinding;
    private DramaEpisodesPlayerUiFragment mFragment;
    String dramaId;
    int episodesNum;
    private DramaPrice mDramaPrice;

    public static void start(Context context, String dramaId, int episodesNum, String cover, long playerPosition) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(DRAMA_ID, dramaId);
        intent.putExtra(DRAMA_EPISODES_NUM, episodesNum);
        intent.putExtra(DRAMA_COVER, cover);
        intent.putExtra(DRAMA_PLAYER_POSITION, playerPosition);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        mBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = getIntent();
        dramaId = intent.getStringExtra(DRAMA_ID);
        episodesNum = intent.getIntExtra(DRAMA_EPISODES_NUM, 0);
        String cover = intent.getStringExtra(DRAMA_COVER);
        long position = intent.getLongExtra(DRAMA_PLAYER_POSITION, 0l);


        PlayerUiStyle playerUiStyle = new PlayerUiStyle.Builder()
                .setTopBackgroundColor(getResources().getColor(R.color.black))
                .setBackgroundColor(getResources().getColor(R.color.black))
                .setBufferedColor(Color.parseColor("#6C6C6C"))
                .setUnPlayedColor(Color.parseColor("#525252"))
                .setBackRes(R.drawable.ic_back)
                .setCollect(getResources().getString(R.string.collect))
                .setCollectSelected(getResources().getString(R.string.collect_add))
                .setEpisodes(getResources().getString(R.string.episodes))
                .setLoadingRes(R.layout._video_loading)
                .build();

        if (savedInstanceState == null) {
            mFragment = JOWOSdk.getDramaEpisodesPlayerFragment(dramaId, episodesNum, cover, position, true, true, playerUiStyle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
            // 设置回调后不再走全局回调
            mFragment.setOnPlayerListener(onPlayerListener);
            mFragment.setOnShowListener(onShowListener);
        }

        DramaServiceImpl.get().getDramaPrice().observe(this, result -> {
            mDramaPrice = result;
        });

    }

    private final Listener.OnPlayerListener onPlayerListener = new Listener.OnPlayerListener() {
        @Override
        public void initOk() {

        }

        @Override
        public void start(Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "start", drama.getId(), episodes.getEpisodesNum());
        }

        @Override
        public void pause(Drama drama, Episodes episodes, long currentPosition) {
            LogUtils.dTag(TAG, "pause", drama.getId(), episodes.getEpisodesNum());
        }

        @Override
        public void finish(Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "finish", drama.getId(), episodes.getEpisodesNum());
        }


        @Override
        public void error(String id, int num, int code, String msg) {
            LogUtils.dTag(TAG, "error", id, num, code, msg);
            ToastUtils.showLong(msg);
            ThreadUtils.runOnUiThreadDelayed(PlayerActivity.this::finish, 500);
        }

        @Override
        public void controllerClick(int action) {
            LogUtils.dTag(TAG, "controllerClick", action);
            if (action == ACTION_BACK) {
                PlayerActivity.this.finish();
            }
        }
    };

    private final Listener.OnShowListener onShowListener = new Listener.OnShowListener() {
        @Override
        public void showEpisodesList(Context context, Drama drama, int currentNum) {
            LogUtils.dTag(TAG, "showEpisodesList", drama.getId(), currentNum);
            showEpisodesListDialog(drama);
        }

        @Override
        public void showUnlock(Context context, Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "showUnlock", drama.getId());
            showUnlockDialog(PlayerActivity.this, drama, episodes);
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
        public void showMenu(Context context, View view, Drama drama, HashMap<String, Object> extras) {
            int num = (int) extras.get("episodes_num");
            LogUtils.dTag(TAG, "showMenu", drama.getId(), num);

            PopupMenu popup = new PopupMenu(context, view);
            popup.getMenuInflater().inflate(R.menu.player_pop_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                LoadingDialog.showLoading(context, false);
                JOWOSdk.fetchEpisodeShareUrl(drama.getJowoVid(), num, new Callback<EpisodeShareUrl>() {
                    @Override
                    public void result(Result<EpisodeShareUrl> result) {
                        LoadingDialog.dismissLoading();
                        EpisodeShareUrl data = result.getData();
                        String shareUrl = data == null ? "" : data.getUrl();
                        int itemId = item.getItemId();
                        if (itemId == R.id.copy) {
                            ClipboardUtils.copyText(shareUrl);
                        } else if (itemId == R.id.share) {
                            String content = "Share you a video: " + shareUrl;
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT, content);
                            context.startActivity(
                                    Intent.createChooser(
                                            textIntent, "Share Video"
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            );
                        }
                    }
                });

                return true;
            });
            popup.show();

        }

        @Override
        public void showLastPageUp() {
            LogUtils.dTag(TAG, "showLastPageUp");
            ToastUtils.showLong("最后一集上滑，可添加更多剧集推荐");
        }
    };

    private void showEpisodesListDialog(Drama drama) {
        DramaEpisodesActivity.start(this, drama.getJowoVid(), 0, drama.getCover(), 0l);

    }

//    private void showUnlockDialog(Drama drama, Episodes episodes) {
//        DramaEpisodesActivity.start(this, drama.getId(), episodes.getEpisodesNum(), drama.getCover(), 0l);
//    }


    private String mVipDramaId;
    private int mVipNum;
    CoinUnlockDialog coinUnlockDialog;
    UnlockDialog unlockDialog;

    private void showUnlockDialog(Context context, Drama drama, Episodes episodes) {
        if (VipManager.isVip()) {
            if (!(Objects.equals(mVipDramaId, drama.getJowoVid()) && mVipNum == episodes.getEpisodesNum())) {
                mVipDramaId = drama.getJowoVid();
                mVipNum = episodes.getEpisodesNum();
                SDKExt.playEpisodes(context, drama.getJowoVid(), episodes.getEpisodesNum(), useLanguage());
            }

            return;
        }

        if (mDramaPrice != null) {
            if (coinUnlockDialog != null && coinUnlockDialog.isShowing()) {
                return;
            }
            int consumeCoin = mDramaPrice.getCoin(drama.getLevel());
            LogUtils.dTag("FWFW", consumeCoin, drama.getLevel());
            if (consumeCoin > -1 && Current.getCoin() >= consumeCoin) {
                // 金币解锁
                if (VipManager.isAutoUnlock()) {
                    // 自动解锁
                    coinUnlock(context, drama, episodes);
                } else {
                    coinUnlockDialog = new CoinUnlockDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt(RouterHub.EXTRA_DRAMA_COIN, consumeCoin);
                    coinUnlockDialog.setArguments(bundle);
                    coinUnlockDialog.setOnClickListener(new CoinUnlockDialog.OnClickListener() {
                        @Override
                        public void unlock() {
                            coinUnlockDialog.dismiss();
                            coinUnlock(context, drama, episodes);
                        }
                    });
//                    dialog.setDialogDismissListener(() -> {
////                        int num = episodes.getEpisodesNum() - 1;
////                        JOWOSdk.playDrama(context, drama.getId(), drama.getCover(), num, 0);
//                        showToast(R.string.unlock_tip);
//                    });
                    coinUnlockDialog.show(context);
                }
                return;
            }
        }

        if (unlockDialog != null && unlockDialog.isShowing()) {
            return;
        }
        unlockDialog = new UnlockDialog();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_DRAMA_ID, drama.getJowoVid());
        bundle.putInt(RouterHub.EXTRA_EPISODES_NUM, episodes.getEpisodesNum());
        unlockDialog.setArguments(bundle);
        unlockDialog.setOnClickListener(new UnlockDialog.OnClickListener() {
            @Override
            public void unlock(DramaNumUnlock dramaNumUnlock) {
                if (dramaNumUnlock.getAdCount() >= DramaNumUnlock.getUnlockAdCount()) {
                    JOWOSdk.unlockEpisodes(drama.getJowoVid(), episodes.getEpisodesNum(), new Callback<Episodes>() {
                        @Override
                        public void result(Result<Episodes> result) {
                            unlockDialog.dismissAllowingStateLoss();
                        }
                    });
                } else {
                    if (RewardVideoAutoAd.isReady(PlayerActivity.this, Constants.AD_REWARD_CODE)) {
                        RewardVideoAutoAd.show(PlayerActivity.this, Constants.AD_REWARD_CODE, new RewardVideoAutoAd.AutoEventListener() {
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
                                ViewUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int adCount = unlockDialog.adCountPlus();
                                        if (adCount >= DramaNumUnlock.getUnlockAdCount()) {
                                            JOWOSdk.unlockEpisodes(drama.getJowoVid(), episodes.getEpisodesNum(), new Callback<Episodes>() {
                                                @Override
                                                public void result(Result<Episodes> result) {
                                                    unlockDialog.dismissAllowingStateLoss();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        ToastUtils.showShort(R.string.no_reward_ad);
                    }

                }
            }

            @Override
            public void vip(boolean suc) {
                if (suc) {
                    unlockDialog.dismissAllowingStateLoss();
                    SDKExt.playEpisodes(context, drama.getJowoVid(), episodes.getEpisodesNum(), useLanguage());
                }
            }

            @Override
            public void topUp() {
                unlockDialog.dismissAllowingStateLoss();
                coinUnlock(context, drama, episodes);
            }
        });
//        dialog.setDialogDismissListener(() -> {
//            int num = episodes.getEpisodesNum() - 1;
//            JOWOSdk.playDrama(context, drama.getId(), drama.getCover(), num, 0);
//            showToast(R.string.unlock_tip);
//        });
        unlockDialog.show(context);
    }

    private void coinUnlock(Context context, Drama drama, Episodes episodes) {
        // TODO loading处理
        LoadingDialog.showLoading(context, false);
//        RequestUnlockDrama requestUnlockDrama = new RequestUnlockDrama();
//        requestUnlockDrama.setDramaId(drama.getId());
//        requestUnlockDrama.setDramaCover(drama.getCover());
//        requestUnlockDrama.setDramaName(drama.getName());
//        requestUnlockDrama.setDramaLevel(drama.getLevel());
//        requestUnlockDrama.setDramaEpisodeNum(episodes.getEpisodesNum());
//        mDramaId = drama.getId();
//        mNum = episodes.getEpisodesNum();
//        consumeCoin = mDramaPrice.getCoin(drama.getLevel());
//        mViewModel.postUnlockDrama(requestUnlockDrama).observe(MainActivity.this, result -> {
//
//        });
        ThreadUtils.runOnUiThreadDelayed(() -> {
            LoadingDialog.dismissLoading();
            ToastUtils.showShort(R.string.unlock_no_coin);
        }, 3 * 1000);
    }


    private void unlock(String id, int episodesNum, String language) {
        JOWOSdk.unlockEpisodes(id, episodesNum, new Callback() {
            @Override
            public void result(Result result) {
                if (result.getCode() == Result.Code.OK) {
                    mFragment.videoPlay(episodesNum);
                } else {
                    ToastUtils.showLong(R.string.unlock_episodes_fail);
                }
            }
        });
    }


}
