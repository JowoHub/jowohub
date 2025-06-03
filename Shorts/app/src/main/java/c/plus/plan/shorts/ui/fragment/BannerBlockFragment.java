package c.plus.plan.shorts.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Banner;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.ad.AdNative;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.FragmentBannerBlockBinding;
import c.plus.plan.shorts.manager.ShortsEventManager;
import c.plus.plan.shorts.remoteconfig.AdConfig;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.adapter.DramaBannerAdapter;
import c.plus.plan.shorts.ui.adapter.DramaGridAdapter;
import c.plus.plan.shorts.ui.view.CommonStaggeredGridLayoutManager;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;
import c.plus.plan.shorts.ui.view.GridSpacingItemDecoration;

public class BannerBlockFragment extends Fragment {
    private FragmentBannerBlockBinding mBinding;
    private String mBlockKey;
    private int mCursorId = 0;
    private DramaGridAdapter mAdapter;
    private final List<Object> mOldList = new ArrayList<>();
    private final List<Object> mNewList = new ArrayList<>();
    private DramaBannerAdapter mBannerAdapter;
    private final List<Banner> mBannerList = new ArrayList<>();
    private AdNative adNative;
    private int firstVisible;
    private int lastVisible;
    public static final int INTERVAL = 3;
    public static final int FIRST = 1;
    private int newState;
    private int adCount;
    private boolean isLoadAd;
    private final List<Integer> adPositions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentBannerBlockBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    public static BannerBlockFragment newInstance(String blockKey) {
        BannerBlockFragment fragment = new BannerBlockFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RouterHub.EXTRA_BLOCK_KEY, blockKey);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBlockKey = getArguments().getString(RouterHub.EXTRA_BLOCK_KEY);

        initViews();
        loadCache();
        mBinding.srl.autoRefresh();
    }

    private void startFetch() {
        mBinding.noNetwork.getRoot().setVisibility(View.GONE);
        mBinding.noData.getRoot().setVisibility(View.GONE);
        fetchData();
        fetchBannerList();
    }

    private void initViews() {
        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            startFetch();
        });
//
        mBinding.srl.setOnLoadMoreListener(refreshLayout -> {
            fetchData();
        });

        mBinding.noNetwork.retry.setOnClickListener(view -> {
            mBinding.srl.autoRefresh(0);
//            startFetch();
        });

        mAdapter = new DramaGridAdapter();
        int spanCount = (ScreenUtils.getScreenWidth()) / SizeUtils.dp2px(140);
        int spacing = SizeUtils.dp2px(10); // 间距
        boolean includeEdge = true; // 是否包括边缘
        int realItemWidth = (ScreenUtils.getScreenWidth() - (spanCount + 1) * spacing) / spanCount;
        int itemHeight = 90 * realItemWidth / 160;
        mAdapter.setImageHeight(itemHeight);
        mAdapter.setImageWidth(realItemWidth);
//        CommonFlexboxLayoutManager layoutManager = new CommonFlexboxLayoutManager(getContext());
//        layoutManager.setFlexDirection(FlexDirection.ROW);
//        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        CommonStaggeredGridLayoutManager lm = new CommonStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.rv.setLayoutManager(lm);
        mBinding.rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mBinding.rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DramaGridAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Drama item) {
                Bundle bundle = new Bundle();
                bundle.putString("type", mBlockKey);
                bundle.putInt("id", item.getId());
                ShortsEventManager.onEvent("home_click", bundle);
                SDKExt.playDrama(getContext(), item.getJowoVid(), item.getCover(), 0, 0);
            }
        });

        mBannerAdapter = new DramaBannerAdapter(mBannerList);
        mBinding.banner.addBannerLifecycleObserver(getViewLifecycleOwner())
                .addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position < mBannerList.size() && mBannerList.get(position) != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("key", "display");
                            bundle.putInt("id", mBannerList.get(position).getId());
                            ShortsEventManager.onEvent("banner_event", bundle);
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                })
                .setAdapter(mBannerAdapter);
        mBinding.banner.setBannerGalleryEffect(30, 8, 0.8f);
        mBannerAdapter.setOnItemClickListener(new DramaBannerAdapter.OnItemClickListener() {
            @Override
            public void click(Banner banner) {
                if (banner.getDrama() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", "click");
                    bundle.putInt("id", banner.getDrama().getId());
                    ShortsEventManager.onEvent("banner_event", bundle);
                    SDKExt.playDrama(getContext(), banner.getDrama().getJowoVid(), banner.getDrama().getCover(), banner.getEpisodesNum(), 0);
                }
            }
        });

        mBinding.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                BannerBlockFragment.this.newState = newState;
                checkNeedAddNativeAdToData();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        initNative();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Object object : mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Object object : mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onPause();
            }
        }
    }

    private void initNative() {
        adNative = new AdNative(getActivity(), Constants.AD_FEED_CODE);
        loadAd();
    }

    private void loadAd() {
        if (adNative != null && !adNative.isReady() && !isLoadAd) {
            isLoadAd = true;
            adNative.loadAd();
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
            AdNative AdNative;
            int index = 0;
            for (Integer adPosition : adPositions) {
                if (i == adPosition - index) {
                    AdNative = getNativeAd();
                    if(AdNative != null && mNewList.size() > adPosition && i >= 0){
                        mOldList.clear();
                        mOldList.addAll(mNewList);
                        mNewList.add(i, AdNative);
                        updateAdapter();
                        adPositions.remove(adPosition);
                    }

                    loadAd();
                    break;
                }
                index++;
            }
        } catch (Exception e) {

        }
//        if(mNewList.get(i) == null){
//            AdNative = getNativeAd();
//            if(AdNative != null){
//                mNewList.set(i, AdNative);
//                mAdapter.notifyItemChanged(i);
//            }
//            loadAd();
//        }
    }

    private AdNative getNativeAd() {
        AdNative adNative = null;
        if (this.adNative != null) {
            adNative = this.adNative;
            if (adNative == null) {
                loadAd();
            }
        }
        return adNative;
    }

    private void addNativeAdToData() {
        for (int i = firstVisible; i < lastVisible; i++) {
            addAndReloadAds(i);
        }
    }

    private void setStopScrollVisibleCount() {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mBinding.rv.getLayoutManager();
        if (layoutManager != null) {
            int[] fs = layoutManager.findFirstVisibleItemPositions(null);
            if (fs != null && fs.length > 0) {
                firstVisible = fs[0];
            }

            int[] ls = layoutManager.findLastVisibleItemPositions(null);
            if (ls != null && ls.length > 0) {
                lastVisible = ls[0];
            }
        }
    }

    private void fetchData() {
        JOWOSdk.fetchDramasByBlockKeyPerPage(mBlockKey, mCursorId, result -> {
            mBinding.srl.finishRefresh();
            if (result.getCode() == Result.Code.OK) {
                if (mCursorId == 0 && CollectionUtils.isEmpty(result.getData().getContent()) && mNewList.isEmpty()) {
                    mBinding.noData.getRoot().setVisibility(View.VISIBLE);
                    return;
                }
                mOldList.clear();
                mOldList.addAll(mNewList);
                boolean refresh = false;
                if (mCursorId == 0) {
                    refresh = true;
//                    mBinding.srl.setEnableRefresh(false);
//                    mBinding.srl.removeView(mBinding.header);
                    for (Object object : mNewList) {
                        if(object instanceof AdNative){
                            AdNative item = (AdNative) object;
                            item.onDestroy();
                        }
                    }
                    mNewList.clear();
                }
//                mNewList.addAll(result.getData().getContent());
                addData(result.getData().getContent());
                mCursorId = result.getData().getCursorId();
                if (result.getData().isLast()) {
                    mBinding.srl.finishLoadMoreWithNoMoreData();
                } else {
                    mBinding.srl.finishLoadMore();
                }

                if (refresh) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    updateAdapter();

                }
            } else {
                mBinding.srl.finishLoadMore();
                if (mNewList.isEmpty()) {
                    mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateAdapter() {
        mAdapter.setData(mNewList);
        ThreadUtils.executeByFixed(Constants.THREAD_SIZE, new ThreadUtils.SimpleTask<DiffUtil.DiffResult>() {
            @Override
            public DiffUtil.DiffResult doInBackground() throws Throwable {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EqualsDiffCallBack<>(mOldList, mNewList), true);
                return diffResult;
            }

            @Override
            public void onSuccess(DiffUtil.DiffResult result) {
                result.dispatchUpdatesTo(mAdapter);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (adNative != null) {
            adNative.onDestroy();
        }
        for (Object object : mNewList) {
            if(object instanceof AdNative){
                AdNative item = (AdNative) object;
                item.onDestroy();
            }
        }
        super.onDestroy();
    }

    private void loadCache() {
        JOWOSdk.lastDramasByBlockKey(mBlockKey, result -> {
            if (result.getCode() == Result.Code.OK) {
                mNewList.addAll(result.getData());
                updateAdapter();
            } else {
                // TODO 失败处理
            }
        });

        JOWOSdk.lastBanner(result -> {
            if (result.getCode() == Result.Code.OK) {
                mBannerList.addAll(result.getData());
                if (mBannerList.isEmpty()) {
                    mBinding.banner.setVisibility(View.GONE);
                } else {
                    mBinding.banner.setVisibility(View.VISIBLE);
                    mBinding.banner.getAdapter().setDatas(mBannerList);
                    mBinding.banner.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    private void fetchBannerList() {
        JOWOSdk.fetchBanner(new Callback<>() {
            @Override
            public void result(Result<List<Banner>> result) {
                if (result.getCode() == Result.Code.OK) {
                    if (CollectionUtils.isEmpty(result.getData())) {
                        if (mBannerList.isEmpty()) {
                            mBinding.banner.setVisibility(View.GONE);
                        }
                    } else {
                        mBinding.banner.setVisibility(View.VISIBLE);
                        mBannerList.addAll(result.getData());
                        mBinding.banner.getAdapter().setDatas(mBannerList);
                        mBinding.banner.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addData(List<Drama> dramas) {
        int start = mNewList.size();
        if (start == 0) {
            adPositions.clear();
        }
        mNewList.addAll(dramas);
        int i = AdConfig.get().getHomeTabListAd().getFirst() * 2; // 插入在第i个位置
        int j = AdConfig.get().getHomeTabListAd().getInterval() * 2; // 每隔j个插入

        // 插入元素
        int currentIndex = i;
        while (currentIndex <= mNewList.size()) {
            if (currentIndex > start) {
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
}
