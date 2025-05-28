package c.plus.plan.skits.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.jowosdk.entity.Label;
import c.plus.plan.skits.ui.fragment.BlockFragment;
import c.plus.plan.skits.ui.fragment.LabelFragment;

public class LabelViewPagerAdapter  extends FragmentStateAdapter {
    private List<Label> labels;

    public LabelViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public LabelViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Label label = labels.get(position);
        return LabelFragment.newInstance(label.getLabelKey());
    }

    @Override
    public int getItemCount() {
        return labels == null ? 0 : labels.size();
    }
}
