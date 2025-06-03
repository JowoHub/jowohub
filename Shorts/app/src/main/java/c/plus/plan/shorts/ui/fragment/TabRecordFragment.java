package c.plus.plan.shorts.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.FragmentTabRecordBinding;
import c.plus.plan.shorts.sdk.SDKExt;
import c.plus.plan.shorts.ui.activity.HistoryActivity;
import c.plus.plan.shorts.ui.activity.SettingsActivity;
import c.plus.plan.shorts.ui.adapter.CollectAdapter;
import c.plus.plan.shorts.ui.adapter.HistoryAdapter;
import c.plus.plan.shorts.ui.entity.Menu;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;
import c.plus.plan.shorts.ui.view.MenuDialog;

public class TabRecordFragment extends BaseFragment {
    private FragmentTabRecordBinding mBinding;
    private HistoryAdapter mHistoryAdapter;
    private CollectAdapter mCollectAdapter;
    private int mCursorId = 0;
    private final List<Drama> mOldList = new ArrayList<>();
    private final List<Drama> mNewList = new ArrayList<>();
    private boolean isLoad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabRecordBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void loadHistory() {
        JOWOSdk.lastHistories(result -> {
            if(CollectionUtils.isEmpty(result.getData())){
                mBinding.noDataHistory.getRoot().setVisibility(View.VISIBLE);
                mBinding.rvHistory.setVisibility(View.GONE);
            } else {
                mBinding.noDataHistory.getRoot().setVisibility(View.GONE);
                mBinding.rvHistory.setVisibility(View.VISIBLE);
                mHistoryAdapter.setDramaHistories(result.getData());
                mHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewList.isEmpty() || !isLoad){
            mBinding.srl.autoRefresh(0);
        }
        loadHistory();
    }

    private void initViews() {
        BarUtils.addMarginTopEqualStatusBarHeight(mBinding.top);
        mHistoryAdapter = new HistoryAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvHistory.setAdapter(mHistoryAdapter);
        mBinding.rvHistory.setLayoutManager(layoutManager);
        mHistoryAdapter.setOnItemClickListener((position, item) -> {
            SDKExt.playDrama(getContext(), item.getDrama().getJowoVid(), item.getDrama().getCover(), item.getEpisodes().getEpisodesNum(), item.getProgressTimeMillis());
        });

        mCollectAdapter = new CollectAdapter();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setAdapter(mCollectAdapter);
        mBinding.rv.setLayoutManager(layoutManager1);
        mCollectAdapter.setOnItemClickListener(new CollectAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Drama item) {
                SDKExt.playDrama(getContext(), item.getJowoVid(), item.getCover(), 0, 0);
            }

            @Override
            public void menu(int position, Drama item) {
                showMenuDialog(position, item);
            }

            @Override
            public void checked(int position, Drama item, boolean isChecked) {

            }
        });

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

        mBinding.more.setOnClickListener(v -> {
            HistoryActivity.start(getContext());
        });

        mBinding.setting.setOnClickListener(v -> {
            SettingsActivity.start(getContext());
        });
    }

    private void showMenuDialog(int position,Drama item) {
        MenuDialog dialog = new MenuDialog();
        Bundle bundle = new Bundle();
        ArrayList<Menu> menuList = new ArrayList();
        menuList.add(new Menu(getResources().getString(R.string.delete)));
        bundle.putParcelableArrayList(RouterHub.EXTRA_MENU, menuList);
        dialog.setArguments(bundle);
        dialog.setOnClickListener(new MenuDialog.OnClickListener() {
            @Override
            public void click(Menu menu) {
                dialog.dismiss();
                JOWOSdk.deleteDramaCollectCancel(item.getJowoVid(), result -> {
                    if(result.getCode() == Result.Code.OK){
                        showToast(R.string.cancel_collect_drams_suc);
                        mOldList.clear();
                        mOldList.addAll(mNewList);
                        mNewList.remove(position);
                        updateAdapter();
                    }
                });
            }
        });
        dialog.show(getContext());
    }

    private void fetchData() {
        JOWOSdk.fetchUserDramaCollectsPerPage(mCursorId, result -> {
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
                mBinding.noNetwork.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateAdapter() {
        mCollectAdapter.setDramas(mNewList);
        ThreadUtils.executeByFixed(Constants.THREAD_SIZE, new ThreadUtils.SimpleTask<DiffUtil.DiffResult>()
        {
            @Override
            public DiffUtil.DiffResult doInBackground () throws Throwable {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EqualsDiffCallBack<>(mOldList, mNewList), true);
                return diffResult;
            }

            @Override
            public void onSuccess (DiffUtil.DiffResult result){
                result.dispatchUpdatesTo(mCollectAdapter);
            }
        });
    }
}
