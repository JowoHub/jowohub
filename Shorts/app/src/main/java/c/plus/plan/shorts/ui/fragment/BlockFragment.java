package c.plus.plan.shorts.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
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
import c.plus.plan.shorts.ad.AdNative;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.FragmentBlockBinding;
import c.plus.plan.shorts.manager.ShortsEventManager;
import c.plus.plan.shorts.remoteconfig.AdConfig;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.adapter.DramaNativeAdAdapter;
import c.plus.plan.shorts.ui.view.CommonGridLayoutManager;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;
import c.plus.plan.shorts.ui.view.GridSpacingItemDecoration;


public class BlockFragment extends Fragment {
    private FragmentBlockBinding mBinding;
    private String mBlockKey;
    private int mCursorId = 0;
    private DramaNativeAdAdapter mAdapter;
    private List<Object> mOldList = new ArrayList<>();
    private List<Object> mNewList = new ArrayList<>();
    private boolean isLoad;
    private AdNative tpNative;
    private int firstVisible;
    private int lastVisible;
    //0 inside; 1 up; 2;down;3 up down
    public static final int STATUS_INSIDE = 0;
    private int addAdsStatus = STATUS_INSIDE;
    private int newState;
    private int adCount;
    private boolean isLoadAd;
    private final List<Integer> adPositions = new ArrayList<>();


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
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Object object: mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onResume();
            }
        }
        if(mNewList.isEmpty() || !isLoad){
            mBinding.srl.autoRefresh(0);
        }
    }

    private void initViews() {
        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            mBinding.noNetwork.getRoot().setVisibility(View.GONE);
            mBinding.noData.getRoot().setVisibility(View.GONE);
            fetchData();
        });

        mBinding.srl.setOnLoadMoreListener(refreshLayout -> {
            fetchData();
        });

        mBinding.noNetwork.retry.setOnClickListener(view -> {
            mBinding.srl.autoRefresh(0);
        });


        mAdapter = new DramaNativeAdAdapter();
        int spanCount = (ScreenUtils.getScreenWidth())/ SizeUtils.dp2px(330);
        int spacing = SizeUtils.dp2px(15); // 间距
        boolean includeEdge = true; // 是否包括边缘
        int realItemWidth = (ScreenUtils.getScreenWidth() - 2*spacing)/spanCount;
        int itemHeight = 90*realItemWidth/160;
        mAdapter.setImageHeight(itemHeight);
        mAdapter.setTitleLines(1);
        mBinding.rv.setLayoutManager(new CommonGridLayoutManager(getContext(), spanCount));
        mBinding.rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mBinding.rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DramaNativeAdAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Drama item) {
                Bundle bundle = new Bundle();
                bundle.putString("type", mBlockKey);
                bundle.putInt("id", item.getId());
                ShortsEventManager.onEvent("home_click", bundle);
                SDKExt.playDrama(getContext(), item.getJowoVid(), item.getCover(), 0, 0);
            }
        });

        mBinding.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                BlockFragment.this.newState = newState;
                checkNeedAddNativeAdToData();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        initNative();
    }

    private void initNative() {
        tpNative = new AdNative(getActivity(), Constants.AD_FEED_CODE);
        loadAd();
    }

    private void loadAd() {
        if (tpNative != null && !tpNative.isReady() && !isLoadAd) {
            tpNative.loadAd();
            isLoadAd = true;
        }
    }

    private void checkNeedAddNativeAdToData() {
        if (newState == 0) {
            setStopScrollVisibleCount();

            addNativeAdToData();
        }
    }

    //可视范围内判断是否需要添加
    private void addAndReloadAds(int i) {
        try {
            AdNative tpCustomNativeAd;
            int index = 0;
            for (Integer adPosition: adPositions) {
                if(i == adPosition - index){
                    tpCustomNativeAd = getNativeAd();
                    if(tpCustomNativeAd != null && mNewList.size() > adPosition && i >= 0){
                        mOldList.clear();
                        mOldList.addAll(mNewList);
                        mNewList.add(i, tpCustomNativeAd);
                        updateAdapter();
                        adPositions.remove(adPosition);
                    }

                    loadAd();
                    break;
                }
                index ++;
            }
        } catch (Exception e){

        }
    }

    private AdNative getNativeAd() {
        AdNative tpCustomNativeAd = null;
        if (tpNative != null) {
            tpCustomNativeAd = tpNative;
            if (tpCustomNativeAd == null) {
                loadAd();
            }
        }
        return tpCustomNativeAd;
    }

    private void addNativeAdToData() {
        for (int i = firstVisible; i < lastVisible; i++) {
            addAndReloadAds(i);
        }
    }

    private void setStopScrollVisibleCount() {
        GridLayoutManager layoutManager = (GridLayoutManager) mBinding.rv.getLayoutManager();
        if (layoutManager != null) {
            firstVisible = layoutManager.findFirstVisibleItemPosition();
            lastVisible = layoutManager.findLastVisibleItemPosition();
        }
    }

    @Override
    public void onDestroy() {
        if(tpNative != null){
            tpNative.onDestroy();
        }
        for (Object object: mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onDestroy();
            }
        }
        super.onDestroy();
    }


    private void fetchData() {
        JOWOSdk.fetchDramasByBlockKeyPerPage(mBlockKey, mCursorId, result -> {
            mBinding.srl.finishRefresh();
            if(result.getCode() == Result.Code.OK){
                isLoad = true;
                if(mCursorId == 0 && CollectionUtils.isEmpty(result.getData().getContent()) && mNewList.isEmpty()){
                    mBinding.noData.getRoot().setVisibility(View.VISIBLE);
                    return;
                }
                mOldList.clear();
                mOldList.addAll(mNewList);

                if(mCursorId == 0){
                    for (Object object: mNewList) {
                        if(object instanceof AdNative){
                            AdNative item = (AdNative) object;
                            item.onDestroy();
                        }
                    }
                    mNewList.clear();
                }
                addData(result.getData().getContent());
//                mNewList.addAll(result.getData().getContent());
                mCursorId = result.getData().getCursorId();
                if(result.getData().isLast()){
                    mBinding.srl.finishLoadMoreWithNoMoreData();
                } else {
                    mBinding.srl.finishLoadMore();
                }

                updateAdapter();
            } else {
                mBinding.srl.finishLoadMore();
                if(mNewList.isEmpty()){
                    mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addData(List<Drama> dramas) {
        int start = mNewList.size();
        if(start == 0){
            adPositions.clear();
        }
        mNewList.addAll(dramas);
        int i = AdConfig.get().getHomeTabListAd().getFirst(); // 插入在第i个位置
        int j = AdConfig.get().getHomeTabListAd().getInterval(); // 每隔j个插入

        // 插入元素
        int currentIndex = i;
        while (currentIndex <= mNewList.size()) {
            if(currentIndex > start){
//                mNewList.add(currentIndex, null);
                adPositions.add(currentIndex);
            }
            currentIndex += (j + 1); // 因为插入后，元素的索引都会向后移动一位
        }
        mBinding.rv.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNeedAddNativeAdToData();
            }
        }, 10);
    }

    private void updateAdapter() {
        mAdapter.setData(mNewList);
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

    @Override
    public void onPause() {
        super.onPause();
        for (Object object: mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onPause();
            }
        }
    }

    private void loadCache() {
        JOWOSdk.lastDramasByBlockKey(mBlockKey, result -> {
            if(result.getCode() == Result.Code.OK){
                mNewList.addAll(result.getData());
//                addData(result.getData());
                ViewUtils.runOnUiThread(() -> {
                    updateAdapter();
                });
            } else {
                // TODO 失败处理
            }
        });
    }
}
