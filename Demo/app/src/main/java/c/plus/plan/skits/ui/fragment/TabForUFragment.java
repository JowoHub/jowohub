package c.plus.plan.skits.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.DramaForU;
import c.plus.plan.jowosdk.entity.EpisodeShareUrl;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdk.PlayerUiType;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.jowosdk.sdkui.DramaPlayerUiFragment;
import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.FragmentTabForUBinding;
import c.plus.plan.skits.ui.activity.MainActivity;
import c.plus.plan.skits.ui.activity.PlayerActivity;
import c.plus.plan.skits.ui.adapter.ForuViewPagerAdapter;
import c.plus.plan.skits.ui.view.EpisodesListDialog;
import c.plus.plan.skits.ui.view.UnlockEpisodesDialog;

public class TabForUFragment extends Fragment {
    private static final String TAG = TabForUFragment.class.getSimpleName();
    private FragmentTabForUBinding mBinding;
    private List<DramaForU> mOldList = new ArrayList<>();
    private List<DramaForU> mNewList = new ArrayList<>();
    private boolean firstFetch = true;
    private ForuViewPagerAdapter mViewPagerAdapter;
    private int current;
    private boolean play;
    private MainActivity mActivity;
    private Episodes playingEpisodes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabForUBinding.inflate(inflater, container, false);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (play && TextUtils.equals(mActivity.getCurrentFragment(), RouterHub.FRAGMENT_TAB_FOR_U)) {
//            playCurrent(current);
//        }
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
            mBinding.pager.setCurrentItem(current + 1);
            LogUtils.dTag(TAG, "finish", drama.getJowoVid(), episodes.getEpisodesNum());
        }

        @Override
        public void error(String id, int num, int code, String msg) {
            LogUtils.dTag(TAG, "error", id, num, code, msg);
        }

        @Override
        public void controllerClick(int action) {
            LogUtils.dTag(TAG, "controllerClick", action);

            if (action == Listener.OnPlayerListener.ACTION_DETAIL) {
                play = true;
                DramaForU dramaForU = mNewList.get(current);
                Episodes targetEpisodes = dramaForU.getTargetEpisodes();

                Drama drama = dramaForU.getDrama();

                if (targetEpisodes == null || drama == null) {
                    ToastUtils.showShort("可能出了什么错");
                    return;
                }
                PlayerActivity.start(getContext(), drama.getJowoVid(), targetEpisodes.getEpisodesNum(), targetEpisodes.getCover(), 0);
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
            LogUtils.dTag(TAG, "showEpisodesList", drama.getJowoVid());
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
                PlayerActivity.start(getContext(), drama.getJowoVid(), num, drama.getCover(), 0);
            }
        });
        dialog.show(getContext());
    }

    private void showUnlockDialog(Drama drama, Episodes episodes) {
        UnlockEpisodesDialog dialog = new UnlockEpisodesDialog();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_DRAMA, GsonUtils.toJson(drama));
        bundle.putString(RouterHub.EXTRA_EPISODES, GsonUtils.toJson(episodes));
        dialog.setArguments(bundle);
        dialog.setOnClickListener(() -> {
            dialog.dismiss();
            unlock(drama.getJowoVid(), episodes.getEpisodesNum(), drama.getCover(), LanguageUtils.getSystemLanguage().getLanguage());
        });

        dialog.show(getContext());
    }

    private void unlock(String jowoVid, int episodesNum, String cover, String language) {
        JOWOSdk.unlockEpisodes(jowoVid, episodesNum, new Callback() {
            @Override
            public void result(Result result) {
                if (result.getCode() == Result.Code.OK) {
//                    mFragment.videoPlay(episodesNum);
                    PlayerActivity.start(getContext(), jowoVid, episodesNum, cover, 0);
                } else {
                    ToastUtils.showLong(R.string.unlock_fail);
                }
            }
        });

        // 一次解锁多级
//        JOWOSdk.unlockEpisodesList(id, episodesNum, episodesNum + 4, language, new Callback() {
//            @Override
//            public void result(Result result) {
//                if(result.getCode() == Result.Code.OK){
////                    mFragment.videoPlay(episodesNum);
//                    PlayerActivity.start(getContext(), id, episodesNum, cover, 0);
//                } else {
//                    ToastUtils.showLong(R.string.unlock_fail);
//                }
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        play = true;
    }

    private void fetchData() {
        JOWOSdk.fetchDramasForU(result -> {
            mBinding.srl.finishRefresh();
            if (result.getCode() == Result.Code.OK) {
                if (firstFetch) {
                    mNewList.clear();
                }
                mOldList.clear();
                mOldList.addAll(mNewList);
                mNewList.addAll(result.getData());
                mBinding.srl.finishLoadMore();

                updateAdapter();
            } else {
                mBinding.srl.finishLoadMore();
                // TODO 失败处理
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter() {
        mViewPagerAdapter.setDramaForUList(mNewList);
        if (firstFetch) {
            mViewPagerAdapter.notifyDataSetChanged();
        } else {
            mViewPagerAdapter.notifyItemRangeInserted(mOldList.size(), mNewList.size() - mOldList.size());
        }
    }


    private void initViews() {
        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            firstFetch = true;
            fetchData();
        });

        mBinding.srl.setOnLoadMoreListener(refreshLayout -> {
            firstFetch = false;
            fetchData();
        });


        mViewPagerAdapter = new ForuViewPagerAdapter(this);
        PlayerUiStyle playerUiStyle = new PlayerUiStyle.Builder()
                .setUiType(PlayerUiType.FOR_YOU)
                .setTopBackgroundColor(getResources().getColor(R.color.black))
                .setBackgroundColor(getResources().getColor(R.color.black))
                .setBufferedColor(Color.parseColor("#6C6C6C"))
                .setUnPlayedColor(Color.parseColor("#525252"))
                .setHeaderPaddingTop(-1000)
                .setShowDetail(true)
                .setLoadingRes(R.layout.video_loading)
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
                playingEpisodes = mNewList.get(position).getEpisodes();
                LogUtils.d("DramaForU page selected", position, playingEpisodes.getJowoVid(), playingEpisodes.getEpisodesNum());
                playCurrent(position);
            }
        });
    }

    public Episodes getPlayingEpisodes() {
        return playingEpisodes;
    }

    public void playCurrent() {
        playCurrent(current);
    }

    private void playCurrent(int position) {
        int old = current;
        current = position;
        if (old > 0 && old != position) {
            Fragment fragment = getFragmentByTag("f" + mViewPagerAdapter.getItemId(old));

            if (fragment instanceof DramaPlayerUiFragment) {
                DramaPlayerUiFragment oldFragment = (DramaPlayerUiFragment) fragment;
                oldFragment.videoPause();
            }
        }

        Fragment current = getFragmentByTag("f" + mViewPagerAdapter.getItemId(position));
        if (current instanceof DramaPlayerUiFragment) {
            DramaPlayerUiFragment currentFragment = (DramaPlayerUiFragment) current;
            currentFragment.videoPlay();
        }
    }

    public void preLoad(int count) {
        mBinding.pager.setOffscreenPageLimit(Math.max(count, 1));
    }


    public int getCurrent() {
        return current;
    }

    private Fragment getFragmentByTag(String tag) {
        FragmentManager fragmentManager = getChildFragmentManager();
        return fragmentManager.findFragmentByTag(tag);
    }

}
