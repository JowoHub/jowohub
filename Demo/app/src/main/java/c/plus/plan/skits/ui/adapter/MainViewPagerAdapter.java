package c.plus.plan.skits.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.ui.fragment.TabBlockFragment;
import c.plus.plan.skits.ui.fragment.TabForUFragment;
import c.plus.plan.skits.ui.fragment.TabLabelFragment;
import c.plus.plan.skits.ui.fragment.TabMainFragment;
import c.plus.plan.skits.ui.fragment.TabWebFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private List<String> fragmentNames;

    public MainViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setFragmentNames(List<String> fragmentNames) {
        this.fragmentNames = fragmentNames;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String fragmentName = fragmentNames.get(position);
        Fragment fragment = null;
        if(RouterHub.FRAGMENT_TAB_MAIN.equals(fragmentName)){
            fragment = new TabMainFragment();
        } else if(RouterHub.FRAGMENT_TAB_BLOCK.equals(fragmentName)){
            fragment = new TabBlockFragment();
        } else if(RouterHub.FRAGMENT_TAB_LABEL.equals(fragmentName)){
            fragment = new TabLabelFragment();
        } else if(RouterHub.FRAGMENT_TAB_API.equals(fragmentName)){
            fragment = new Fragment();
        } else if(RouterHub.FRAGMENT_TAB_FOR_U.equals(fragmentName)){
            fragment = new TabForUFragment();
        } else if(RouterHub.FRAGMENT_TAB_H5.equals(fragmentName)){
            fragment = new TabWebFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentNames == null ? 0 : fragmentNames.size();
    }
}
