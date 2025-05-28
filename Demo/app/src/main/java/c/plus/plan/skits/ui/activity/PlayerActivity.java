package c.plus.plan.skits.ui.activity;

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
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.HashMap;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.EpisodeShareUrl;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.jowosdk.sdkui.DramaEpisodesPlayerUiFragment;
import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.ActivityPlayerBinding;
import c.plus.plan.skits.ui.view.EpisodesListDialog;
import c.plus.plan.skits.ui.view.UnlockEpisodesDialog;

public class PlayerActivity extends AppCompatActivity {
    private static final String DRAMA_ID = "id";
    private static final String DRAMA_EPISODES_NUM = "num";
    private static final String DRAMA_COVER = "cover";
    private static final String DRAMA_PLAYER_POSITION = "position";
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private ActivityPlayerBinding mBinding;
    private DramaEpisodesPlayerUiFragment mFragment;

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
        String dramaId = intent.getStringExtra(DRAMA_ID);
        int episodesNum = intent.getIntExtra(DRAMA_EPISODES_NUM, 0);
        String cover = intent.getStringExtra(DRAMA_COVER);
        long position = intent.getLongExtra(DRAMA_PLAYER_POSITION, 0L);

        PlayerUiStyle playerUiStyle = new PlayerUiStyle.Builder()
                .setTopBackgroundColor(getResources().getColor(R.color.red))
                .setBackgroundColor(getResources().getColor(R.color.red))
                .setBufferedColor(Color.parseColor("#6C6C6C"))
                .setUnPlayedColor(Color.parseColor("#525252"))
                .setHeaderPaddingTop(BarUtils.getStatusBarHeight())
                .setLoadingRes(R.layout.video_loading).build();


        if (savedInstanceState == null) {
            mFragment = JOWOSdk.getDramaEpisodesPlayerFragment(dramaId, episodesNum, cover, position, true, true, playerUiStyle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment).commit();
            // 设置回调后不再走全局回调
            mFragment.setOnPlayerListener(onPlayerListener);
            mFragment.setOnShowListener(onShowListener);
        }
    }

    private final Listener.OnPlayerListener onPlayerListener = new Listener.OnPlayerListener() {
        @Override
        public void initOk() {
            LogUtils.dTag(TAG, "initOk");
        }

        @Override
        public void start(Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "start", drama.getJowoVid(), episodes.getEpisodesNum());
        }

        @Override
        public void pause(Drama drama, Episodes episodes, long currentPosition) {
            LogUtils.dTag(TAG, "pause", drama.getJowoVid(), episodes.getEpisodesNum());
        }

        @Override
        public void finish(Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "finish", drama.getJowoVid(), episodes.getEpisodesNum());
        }

        @Override
        public void error(String id, int num, int code, String msg) {
            LogUtils.dTag(TAG, "error", id, num, code, msg);
            ToastUtils.showShort(msg);
            PlayerActivity.this.finish();
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
            LogUtils.dTag(TAG, "showEpisodesList", drama.getJowoVid(), currentNum);
            showEpisodesListDialog(drama);
        }

        @Override
        public void showUnlock(Context context, Drama drama, Episodes episodes) {
            LogUtils.dTag(TAG, "showUnlock", drama.getJowoVid());
            showUnlockDialog(drama, episodes);
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
            LogUtils.dTag(TAG, "showMenu", drama.getJowoVid(), extras);

            PopupMenu popup = new PopupMenu(context, view);
            popup.getMenuInflater().inflate(R.menu.player_pop_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int episodeNum = extras.get("episode_num") != null ? Integer.parseInt(extras.get("episode_num") + "") : 0;
                JOWOSdk.fetchEpisodeShareUrl(drama.getJowoVid(), episodeNum, new Callback<EpisodeShareUrl>() {
                    @Override
                    public void result(Result<EpisodeShareUrl> result) {
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
                            context.startActivity(Intent.createChooser(textIntent, "Share Video").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        EpisodesListDialog dialog = new EpisodesListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_DRAMA, GsonUtils.toJson(drama));
        dialog.setArguments(bundle);
        dialog.setOnClickListener((num, isLock) -> {
            if (isLock) {
                dialog.dismiss();
                Episodes episodes = new Episodes();
                episodes.setEpisodesNum(num);
                showUnlockDialog(drama, episodes);
            } else {
                dialog.dismiss();
                mFragment.videoPlay(num);
            }
        });
        dialog.show(this);
    }

    private void showUnlockDialog(Drama drama, Episodes episodes) {
        UnlockEpisodesDialog dialog = new UnlockEpisodesDialog();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_DRAMA, GsonUtils.toJson(drama));
        bundle.putString(RouterHub.EXTRA_EPISODES, GsonUtils.toJson(episodes));
        dialog.setArguments(bundle);
        dialog.setOnClickListener(() -> {
            dialog.dismiss();
            unlock(drama.getJowoVid(), episodes.getEpisodesNum(), LanguageUtils.getSystemLanguage().getLanguage());
        });

        dialog.show(this);
    }

    private void unlock(String id, int episodesNum, String language) {
        JOWOSdk.unlockEpisodes(id, episodesNum, new Callback() {
            @Override
            public void result(Result result) {
                if (result.getCode() == Result.Code.OK) {
                    mFragment.videoPlay(episodesNum);
                } else {
                    ToastUtils.showLong(R.string.unlock_fail);
                }
            }
        });
    }
}
