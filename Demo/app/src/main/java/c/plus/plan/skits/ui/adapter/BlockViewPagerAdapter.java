package c.plus.plan.skits.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.skits.ui.fragment.BlockFragment;

public class BlockViewPagerAdapter extends FragmentStateAdapter {
    private List<Block> blockList;

    public BlockViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public BlockViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setBlockList(List<Block> blockList) {
        this.blockList = blockList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Block block = blockList.get(position);
        return BlockFragment.newInstance(block.getBlockKey());
    }

    @Override
    public int getItemCount() {
        return blockList == null ? 0 : blockList.size();
    }
}
