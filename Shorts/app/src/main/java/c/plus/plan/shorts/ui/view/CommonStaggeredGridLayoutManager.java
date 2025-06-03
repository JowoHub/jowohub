package c.plus.plan.shorts.ui.view;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.LogUtils;

public class CommonStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public CommonStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e){
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        try {
            super.onScrollStateChanged(state);
        } catch (Exception e){
        }
    }
}
