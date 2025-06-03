package c.plus.plan.shorts.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.blankj.utilcode.util.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.ui.fragment.TabForuFragment;
import c.plus.plan.shorts.ui.fragment.TabHomeFragment;
import c.plus.plan.shorts.ui.fragment.TabMyFragment;
import c.plus.plan.shorts.ui.fragment.TabRecordFragment;
import okhttp3.internal.Util;

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
        if(RouterHub.FRAGMENT_TAB_HOME.equals(fragmentName)){
            fragment = new TabHomeFragment();
        } else if(RouterHub.FRAGMENT_TAB_FORU.equals(fragmentName)){
            fragment = new TabForuFragment();
        } else if(RouterHub.FRAGMENT_TAB_RECORD.equals(fragmentName)){
            fragment = new TabRecordFragment();
        } else if(RouterHub.FRAGMENT_TAB_MY.equals(fragmentName)){
            fragment = new TabMyFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentNames == null ? 0 : fragmentNames.size();
    }

    public void updateTabIcon(TabLayout.Tab tab, int position, boolean s) {
        String fragmentName = fragmentNames.get(position);
        ImageView iv = tab.getCustomView().findViewById(R.id.iv);
        TextView tv = tab.getCustomView().findViewById(R.id.tv);
        if(s){
            if(RouterHub.FRAGMENT_TAB_HOME.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_home));
                iv.setImageResource(R.drawable.ic_tab_home_s);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
            } else if(RouterHub.FRAGMENT_TAB_FORU.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_foru));
                iv.setImageResource(R.drawable.ic_tab_foru_s);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
            } else if(RouterHub.FRAGMENT_TAB_RECORD.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_record));
                iv.setImageResource(R.drawable.ic_tab_record_s);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
            } else if(RouterHub.FRAGMENT_TAB_MY.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_my));
                iv.setImageResource(R.drawable.ic_tab_my_s);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
            }
        } else {
            if(RouterHub.FRAGMENT_TAB_HOME.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_home));
                iv.setImageResource(R.drawable.ic_tab_home);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray));
            } else if(RouterHub.FRAGMENT_TAB_FORU.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_foru));
                iv.setImageResource(R.drawable.ic_tab_foru);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray));
            } else if(RouterHub.FRAGMENT_TAB_RECORD.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_record));
                iv.setImageResource(R.drawable.ic_tab_record);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray));
            } else if(RouterHub.FRAGMENT_TAB_MY.equals(fragmentName)){
                tv.setText(Utils.getApp().getString(R.string.tab_my));
                iv.setImageResource(R.drawable.ic_tab_my);
                tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray));
            }
        }

    }
}
