package c.plus.plan.shorts.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.blankj.utilcode.util.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ui.fragment.BannerBlockFragment;
import c.plus.plan.shorts.ui.fragment.BlockFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    private List<Block> blockList;

    public HomeViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Block block = blockList.get(position);
        if(position == 0){
            return BannerBlockFragment.newInstance(block.getBlockKey());
        } else {
            return BlockFragment.newInstance(block.getBlockKey());
        }
    }

    @Override
    public int getItemCount() {
        return blockList == null ? 0 : blockList.size();
    }

    public void updateTabIcon(TabLayout.Tab tab, int position, boolean select) {
        Block block = blockList.get(position);
        TextView tv = tab.getCustomView().findViewById(R.id.tv);
        tv.setText(block.getName());
        if(select){
            tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
            tv.setTextSize(18);
        } else {
            tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray_900));
            tv.setTextSize(16);
        }
    }
}
