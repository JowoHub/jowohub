package c.plus.plan.shorts.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import c.plus.plan.shorts.ui.fragment.EpisodesFragment;

public class EpisodesViewPagerAdapter extends FragmentStateAdapter {
    private int pageCount;

    public EpisodesViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public EpisodesViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return EpisodesFragment.newInstance(position + 1);
    }


    @Override
    public int getItemCount() {
        return pageCount;
    }
}
