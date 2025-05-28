package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.constants.Constants;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.FragmentLabelBinding;
import c.plus.plan.skits.ui.activity.PlayerActivity;
import c.plus.plan.skits.ui.adapter.DramaGridAdapter;
import c.plus.plan.skits.ui.adapter.EqualsDiffCallBack;
import c.plus.plan.skits.ui.view.GridSpacingItemDecoration;

public class LabelFragment extends Fragment {
    private FragmentLabelBinding mBinding;
    private String mLabelKey;
    private int mCursorId = 0;
    private DramaGridAdapter mAdapter;
    private List<Drama> mOldList = new ArrayList<>();
    private List<Drama> mNewList = new ArrayList<>();

    public static LabelFragment newInstance(String labelKey) {
        LabelFragment fragment = new LabelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_LABEL_KEY, labelKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLabelBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLabelKey = getArguments().getString(RouterHub.EXTRA_LABEL_KEY);

        initViews();
        loadCache();
        mBinding.srl.autoRefresh();
    }

    private void initViews() {
        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            fetchData();
        });

        mBinding.srl.setOnLoadMoreListener(refreshLayout -> {
            fetchData();
        });


        mAdapter = new DramaGridAdapter();
        int spanCount = (ScreenUtils.getScreenWidth())/ SizeUtils.dp2px(140);
        int spacing = SizeUtils.dp2px(10); // 间距
        boolean includeEdge = true; // 是否包括边缘
        mBinding.rv.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mBinding.rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mBinding.rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((position, item) -> {
//            JOWOSdk.playDrama(getContext(), item.getId(), item.getCover(), 0, 0)
            PlayerActivity.start(getContext(), item.getJowoVid(), 0, item.getCover(), 0);
        });
    }



    private void fetchData() {
        JOWOSdk.fetchDramasByLabelKeyPerPage(mLabelKey, mCursorId, result -> {
            mBinding.srl.finishRefresh();
            if(result.getCode() == Result.Code.OK){
                mOldList.clear();
                mOldList.addAll(mNewList);

                if(mCursorId == 0){
                    mNewList.clear();
                }
                mNewList.addAll(result.getData().getContent());
                mCursorId = result.getData().getCursorId();
                if(result.getData().isLast()){
                    mBinding.srl.finishLoadMoreWithNoMoreData();
                } else {
                    mBinding.srl.finishLoadMore();
                }

                LogUtils.dTag("FWFW", GsonUtils.toJson(result));

                updateAdapter();
            } else {
                mBinding.srl.finishLoadMore();
                // TODO 失败处理
            }
        });
    }

    private void updateAdapter() {
        mAdapter.setDramas(mNewList);
        ThreadUtils.executeByFixed(Constants.THREAD_SIZE, new ThreadUtils.SimpleTask<DiffUtil.DiffResult>()
        {
            @Override
            public DiffUtil.DiffResult doInBackground () throws Throwable {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EqualsDiffCallBack<>(mOldList, mNewList), true);
                return diffResult;
            }

            @Override
            public void onSuccess (DiffUtil.DiffResult result){
                result.dispatchUpdatesTo(mAdapter);
            }
        });
    }

    private void loadCache() {
        JOWOSdk.lastDramasByLabelKey(mLabelKey, result -> {
            if(result.getCode() == Result.Code.OK){
                mNewList.addAll(result.getData());
                ViewUtils.runOnUiThread(() -> {
                    updateAdapter();
                });
            } else {
                // TODO 失败处理
            }
        });
    }
}
