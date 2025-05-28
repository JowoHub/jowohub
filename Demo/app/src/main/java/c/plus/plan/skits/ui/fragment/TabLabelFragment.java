package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import c.plus.plan.jowosdk.entity.Label;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.databinding.FragmentTabLabelBinding;
import c.plus.plan.skits.ui.adapter.BlockViewPagerAdapter;
import c.plus.plan.skits.ui.adapter.LabelViewPagerAdapter;

public class TabLabelFragment extends Fragment {
    private FragmentTabLabelBinding mBinding;
    private List<Label> mLabelList;
    private LabelViewPagerAdapter mViewPager;
    private TabLayoutMediator mediator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabLabelBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        loadData();
    }

    private void loadData() {
        JOWOSdk.lastLabels(result -> {
            if(result.getCode() == Result.Code.OK){
                ViewUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLabelList = result.getData();
                        showViewPager();
                    }
                });
            } else {
                // TODO 失败处理
            }
        });

        JOWOSdk.fetchLabels(result -> {
            if(result.getCode() == Result.Code.OK){
                if(CollectionUtils.isEmpty(mLabelList)){
                    mLabelList = result.getData();
                    showViewPager();
                }
            } else {
                // TODO 失败处理
            }
        });
    }

    private void showViewPager() {
        mViewPager.setLabels(mLabelList);
        mediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mLabelList.get(position).getName());
            }
        });
        mediator.attach();
        mViewPager.notifyDataSetChanged();
    }

    private void initViews() {
        mViewPager = new LabelViewPagerAdapter(this);
        mBinding.pager.setAdapter(mViewPager);


    }
}
