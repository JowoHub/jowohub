package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityConsumeRecordBinding;
import c.plus.plan.shorts.entity.UnlockDramaRecord;
import c.plus.plan.shorts.ui.adapter.ConsumeRecordAdapter;
import c.plus.plan.shorts.ui.entity.Menu;
import c.plus.plan.shorts.ui.view.CustomerDialog;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;
import c.plus.plan.shorts.ui.view.MenuDialog;
import c.plus.plan.shorts.ui.viewmodel.TopUpRecordViewModel;

public class ConsumeRecordActivity extends BaseActivity{
    private ActivityConsumeRecordBinding mBinding;
    private final List<UnlockDramaRecord> mOldList = new ArrayList<>();
    private final List<UnlockDramaRecord> mNewList = new ArrayList<>();
    private ConsumeRecordAdapter mAdapter;
    private TopUpRecordViewModel mViewModel;
    private int mCursorId;
    private boolean isLoad;

    public static void start(Context context){
        Intent intent = new Intent(context, ConsumeRecordActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityConsumeRecordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = getActivityScopeViewModel(TopUpRecordViewModel.class);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewList.isEmpty() || !isLoad){
            mBinding.srl.autoRefresh(0);
        }
    }

    private void initViews() {
        mAdapter = new ConsumeRecordAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setAdapter(mAdapter);
        mBinding.rv.setLayoutManager(layoutManager);

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

        mBinding.back.setOnClickListener(v -> finish());
        mBinding.menu.setOnClickListener(v -> showMenu());
    }

    private void showMenu() {
        MenuDialog dialog = new MenuDialog();
        Bundle bundle = new Bundle();
        ArrayList<Menu> menuList = new ArrayList();
        menuList.add(new Menu(getResources().getString(R.string.customer_service)));
        bundle.putParcelableArrayList(RouterHub.EXTRA_MENU, menuList);
        dialog.setArguments(bundle);
        dialog.setOnClickListener(new MenuDialog.OnClickListener() {
            @Override
            public void click(Menu menu) {
                dialog.dismiss();
                if(TextUtils.equals(getResources().getString(R.string.customer_service), menu.getName())){
                   dialog.dismiss();
                   showCustomerDialog();
                }

            }


        });
        dialog.show(this);
    }

    private void showCustomerDialog() {
        CustomerDialog dialog = new CustomerDialog();
        dialog.show(this);
    }

    private void fetchData() {
        mViewModel.requestUnlockDramaRecordList(mCursorId).observe(this, result -> {
            mBinding.srl.finishRefresh();
            if(result.isSuccess()){
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
        mAdapter.setUnlockDramaRecords(mNewList);
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
}
