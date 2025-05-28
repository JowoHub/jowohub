package c.plus.plan.skits.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.DramaForU;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.PlayerUiStyle;
import c.plus.plan.jowosdk.sdkui.DramaPlayerUiFragment;
import c.plus.plan.skits.ui.fragment.ErrorFragment;

public class ForuViewPagerAdapter extends FragmentStateAdapter {
    private List<DramaForU> dramaForUList;
    private PlayerUiStyle style;
    private Listener.OnPlayerListener onPlayerListener;
    private Listener.OnShowListener onShowListener;

    public ForuViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
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
            return ErrorFragment.newInstance("Error","drama is null");
        } else {
            DramaPlayerUiFragment fragment = JOWOSdk.getDramaPlayerFragment(dramaForU.getDrama().getJowoVid(), dramaForU.getEpisodes().getEpisodesNum(), 0, true, false, style);
            // 设置回调后不再走全局回调
            fragment.setOnShowListener(onShowListener);
            fragment.setOnPlayerListener(onPlayerListener);
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
