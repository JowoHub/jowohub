package c.plus.plan.shorts.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.databinding.FragmentTabHomeBinding;
import c.plus.plan.shorts.ui.adapter.HomeViewPagerAdapter;
import c.plus.plan.shorts.ui.view.BlockDiffCallBack;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;

public class TabHomeFragment extends Fragment {
    private FragmentTabHomeBinding mBinding;
    private HomeViewPagerAdapter mViewPager;
    private List<Block> mBlockList;
    private TabLayoutMediator mediator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        loadData();
    }

    private void loadData() {
        JOWOSdk.lastBlocks(result -> {
            if (result.getCode() == Result.Code.OK) {
                ViewUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBlockList = result.getData();
                        showViewPager();
                    }
                });
            }
        });

        JOWOSdk.fetchBlocks(result -> {
            mBinding.noData.getRoot().setVisibility(View.GONE);
            mBinding.noNetwork.getRoot().setVisibility(View.GONE);
            if (result.getCode() == Result.Code.OK) {

                if (CollectionUtils.isEmpty(mBlockList)) {
                    mBlockList = result.getData();
                    showViewPager();

                    if (CollectionUtils.isEmpty(result.getData())) {
                        mBinding.noData.getRoot().setVisibility(View.VISIBLE);
                    }
                } else {
                    ThreadUtils.executeByFixed(Constants.THREAD_SIZE, new ThreadUtils.SimpleTask<Boolean>() {
                        @Override
                        public Boolean doInBackground() throws Throwable {
                            int oldSize = mBlockList == null ? 0 : mBlockList.size();
                            int newSize = result.getData() == null ? 0 : result.getData().size();
                            if (oldSize != newSize) {
                                return true;
                            }
                            for (int i = 0; i < oldSize; i++) {
                                Block oldOne = (Block) mBlockList.get(i);
                                Block newOne = (Block) result.getData().get(i);

                                if (oldOne.getSort() != newOne.getSort()
                                        || (oldOne.getName() != null && !oldOne.getName().equals(newOne.getName()))
                                        || (oldOne.getBlockKey() != null && !oldOne.getBlockKey().equals(newOne.getBlockKey()))) {
                                    return true;
                                }

                            }
                            return false;
                        }

                        @Override
                        public void onSuccess(Boolean ret) {
                            if (ret) {
                                mBlockList = result.getData();
                                showViewPager();

                                if (CollectionUtils.isEmpty(result.getData())) {
                                    mBinding.noData.getRoot().setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
            } else {
                if (CollectionUtils.isEmpty(mBlockList)) {
                    mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void showViewPager() {
        mViewPager.setBlockList(mBlockList);
        mBinding.tab.addOnTabSelectedListener(onTabSelectedListener);
        mediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText(mBlockList.get(position).getName());
                tab.setCustomView(R.layout.item_main_tab);
                updateTabIcon(tab, position, false);
            }
        });
        mediator.attach();
        mViewPager.notifyDataSetChanged();
    }

    private final TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            updateTabIcon(tab, tab.getPosition(), true);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            updateTabIcon(tab, tab.getPosition(), false);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void updateTabIcon(TabLayout.Tab tab, int position, boolean select) {
        mViewPager.updateTabIcon(tab, position, select);
    }

    private void initViews() {
        mViewPager = new HomeViewPagerAdapter(this);
        mBinding.pager.setAdapter(mViewPager);

        mBinding.noNetwork.retry.setOnClickListener(view -> {
            mBinding.noNetwork.getRoot().setVisibility(View.GONE);
            loadData();
        });

        BarUtils.addMarginTopEqualStatusBarHeight(mBinding.ll);
    }
}
