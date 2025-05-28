package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import c.plus.plan.jowosdk.entity.Block;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.databinding.FragmentTabBlockBinding;
import c.plus.plan.skits.ui.adapter.BlockViewPagerAdapter;

public class TabBlockFragment extends Fragment {
    private FragmentTabBlockBinding mBinding;
    private BlockViewPagerAdapter mViewPager;
    private TabLayoutMediator mediator;
    private List<Block> mBlockList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabBlockBinding.inflate(inflater, container, false);
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
            if(result.getCode() == Result.Code.OK){
                ViewUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBlockList = result.getData();
                        showViewPager();
                    }
                });
            } else {
                // TODO 失败处理
            }
        });

        JOWOSdk.fetchBlocks(result -> {
            if(result.getCode() == Result.Code.OK){
                if(CollectionUtils.isEmpty(mBlockList)){
                    mBlockList = result.getData();
                    showViewPager();
                }
            } else {
                // TODO 失败处理
            }
        });
    }

    private void showViewPager() {
        mViewPager.setBlockList(mBlockList);
        mediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mBlockList.get(position).getName());
            }
        });
        mediator.attach();
        mViewPager.notifyDataSetChanged();
    }

    private void initViews() {
        mViewPager = new BlockViewPagerAdapter(this);
        mBinding.pager.setAdapter(mViewPager);


    }
}
