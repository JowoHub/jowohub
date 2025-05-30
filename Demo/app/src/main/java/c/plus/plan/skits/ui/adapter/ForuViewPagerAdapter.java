package c.plus.plan.skits.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.blankj.utilcode.util.LogUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.DramaForU;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdkui.DramaPlayerUiFragment;
import c.plus.plan.skits.ui.fragment.ErrorFragment;
import c.plus.plan.skits.ui.fragment.TabForUFragment;

public class ForuViewPagerAdapter extends FragmentStateAdapter {
    private List<DramaForU> dramaForUList;
    private PlayerUiStyle style;
    private Listener.OnPlayerListener onPlayerListener;
    private Listener.OnShowListener onShowListener;

    private WeakReference<Fragment> fragmentWeakReference;

    public ForuViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        fragmentWeakReference = new WeakReference<>(fragment);
    }

    public ForuViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setDramaForUList(List<DramaForU> dramaForUList) {
        this.dramaForUList = dramaForUList;
    }

    public void setOnPlayerListener(Listener.OnPlayerListener onPlayerListener) {
        this.onPlayerListener = onPlayerListener;
    }

    public void setOnShowListener(Listener.OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    public void setStyle(PlayerUiStyle style) {
        this.style = style;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DramaForU dramaForU = dramaForUList.get(position);
        Drama drama = dramaForU.getDrama();
        if (drama == null) {
            return ErrorFragment.newInstance("Error", "drama is null");
        } else {
            DramaPlayerUiFragment fragment = JOWOSdk.getDramaPlayerFragment(dramaForU.getDrama().getJowoVid(), dramaForU.getEpisodes().getEpisodesNum(), 0, true, false, style);
            // 设置回调后不再走全局回调
            fragment.setOnShowListener(onShowListener);
            fragment.setOnPlayerListener(onPlayerListener);
            fragment.setPlayerCondition(new Listener.PlayerCondition() {

                @Override
                public boolean canPlay(Drama drama, Episodes episodes) {
                    if (fragmentWeakReference.get() instanceof TabForUFragment) {
                        TabForUFragment tabForUFragment = (TabForUFragment) fragmentWeakReference.get();
                        if (tabForUFragment != null) {
                            LogUtils.d("DramaForU", "canPlay: " + tabForUFragment.getPlayingEpisodes().getJowoVid() + " " + tabForUFragment.getPlayingEpisodes().getEpisodesNum() +
                                    " " + episodes.getJowoVid() + " " + episodes.getEpisodesNum());

                            return tabForUFragment.getPlayingEpisodes().getJowoVid().equals(episodes.getJowoVid()) &&
                                    tabForUFragment.getPlayingEpisodes().getEpisodesNum() == episodes.getEpisodesNum();
                        }
                    }
                    return true;

                }
            });
            return fragment;
        }
    }

//    @Override
//    public long getItemId(int position) {
//        DramaForU dramaForU = dramaForUList.get(position);
//        return dramaForU.getId() + 1000;
//    }


    @Override
    public int getItemCount() {
        return dramaForUList == null ? 0 : dramaForUList.size();
    }
}
