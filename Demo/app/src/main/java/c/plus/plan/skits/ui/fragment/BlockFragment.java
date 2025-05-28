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
import c.plus.plan.skits.databinding.FragmentBlockBinding;
import c.plus.plan.skits.ui.activity.PlayerActivity;
import c.plus.plan.skits.ui.adapter.DramaAdapter;
import c.plus.plan.skits.ui.adapter.DramaGridAdapter;
import c.plus.plan.skits.ui.adapter.EqualsDiffCallBack;
import c.plus.plan.skits.ui.view.GridSpacingItemDecoration;

public class BlockFragment extends Fragment {

    private FragmentBlockBinding mBinding;
    private String mBlockKey;
    private int mCursorId = 0;
    private DramaGridAdapter mAdapter;
    private List<Drama> mOldList = new ArrayList<>();
    private List<Drama> mNewList = new ArrayList<>();

    public static BlockFragment newInstance(String blockKey) {
        BlockFragment fragment = new BlockFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_BLOCK_KEY, blockKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentBlockBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBlockKey = getArguments().getString(RouterHub.EXTRA_BLOCK_KEY);

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

        mAdapter.setOnItemClickListener(new DramaAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Drama item) {
//                JOWOSdk.playDrama(getContext(), item.getJowoVid(), item.getCover(), 0, 0);
                PlayerActivity.start(getContext(), item.getJowoVid(), 0, item.getCover(), 0);
            }
        });
    }

    private void fetchData() {
        JOWOSdk.fetchDramasByBlockKeyPerPage(mBlockKey, mCursorId, result -> {
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

//        int index = 0;
//        int count = Math.min(mNewList.size(), 5);
//        int[] ids = new int[count];
//        for (Drama d: mNewList){
//            if(index >= count ){
//                break;
//            }
//            ids[index] = d.getId();
//            index ++;
//        }
//
//        JOWOSdk.fetchDramaList(ids, new Callback<List<Drama>>() {
//            @Override
//            public void result(Result<List<Drama>> result) {
//                LogUtils.dTag("FWFW", GsonUtils.toJson(result));
//            }
//        });
    }

    private void loadCache() {
        JOWOSdk.lastDramasByBlockKey(mBlockKey, result -> {
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
