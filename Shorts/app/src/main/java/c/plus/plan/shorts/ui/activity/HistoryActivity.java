package c.plus.plan.shorts.ui.activity;

import static c.plus.plan.common.service.impl.UserServiceImpl.defaultLanguage;

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
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import c.plus.plan.jowosdk.entity.DramaHistory;
import c.plus.plan.jowosdk.entity.Feedback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.Constants;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityHistoryBinding;
import c.plus.plan.shorts.ui.adapter.HistoryVerticalAdapter;
import c.plus.plan.shorts.ui.entity.Menu;
import c.plus.plan.shorts.ui.view.EqualsDiffCallBack;
import c.plus.plan.shorts.ui.view.MenuDialog;

public class HistoryActivity extends BaseActivity{
    private ActivityHistoryBinding mBinding;
    private HistoryVerticalAdapter mHistoryAdapter;
    private int mCursorId = 0;
    private final List<DramaHistory> mOldList = new ArrayList<>();
    private final List<DramaHistory> mNewList = new ArrayList<>();
    private boolean isLoad;

    public static void start(Context context){
        Intent intent = new Intent(context, HistoryActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
    }

    private void initViews() {
        mHistoryAdapter = new HistoryVerticalAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setAdapter(mHistoryAdapter);
        mBinding.rv.setLayoutManager(layoutManager);

        mHistoryAdapter.setOnItemClickListener(new HistoryVerticalAdapter.OnItemClickListener() {
            @Override
            public void click(int position, DramaHistory item) {
//                JOWOSdk.playDrama(HistoryActivity.this, item.getDrama().getId(), item.getDrama().getCover(), item.getEpisodes().getEpisodesNum(), item.getProgressTimeMillis());
            }

            @Override
            public void menu(int position, DramaHistory item) {
                showMenuDialog(position, item);
            }

            @Override
            public void checked(int position, DramaHistory item, boolean isChecked) {

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

        mBinding.back.setOnClickListener(v -> finish());
    }

    private void showMenuDialog(int position, DramaHistory item) {
        MenuDialog dialog = new MenuDialog();
        Bundle bundle = new Bundle();
        ArrayList<Menu> menuList = new ArrayList();
        menuList.add(new Menu(getResources().getString(R.string.collect)));
        menuList.add(new Menu(getResources().getString(R.string.delete)));
        bundle.putParcelableArrayList(RouterHub.EXTRA_MENU, menuList);
        dialog.setArguments(bundle);
        dialog.setOnClickListener(new MenuDialog.OnClickListener() {
            @Override
            public void click(Menu menu) {
                dialog.dismiss();
                if(TextUtils.equals(getResources().getString(R.string.delete), menu.getName())){
                    JOWOSdk.deleteDramaHistory(item.getDrama().getJowoVid(), result -> {
                        if(result.getCode() == Result.Code.OK){
                            showToast(R.string.delete_drama_history_suc);
                            mOldList.clear();
                            mOldList.addAll(mNewList);
                            mNewList.remove(position);
                            updateAdapter();
                        }
                    });
                } else if(TextUtils.equals(getResources().getString(R.string.collect), menu.getName())){
                    JOWOSdk.postDramaCollect(item.getDrama().getJowoVid(), defaultLanguage(), result -> {
                        if(result.getCode() == Result.Code.OK){
                            showToast(R.string.collect_drama_suc);
                        }
                    });
                }

            }
        });
        dialog.show(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewList.isEmpty() || !isLoad){
            mBinding.srl.autoRefresh(0);
        }
    }

    private void fetchData() {
        JOWOSdk.fetchUserDramaHistoriesPerPage(mCursorId, result -> {
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
        mHistoryAdapter.setDramaHistories(mNewList);
        ThreadUtils.executeByFixed(Constants.THREAD_SIZE, new ThreadUtils.SimpleTask<DiffUtil.DiffResult>()
        {
            @Override
            public DiffUtil.DiffResult doInBackground () throws Throwable {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EqualsDiffCallBack<>(mOldList, mNewList), true);
                return diffResult;
            }

            @Override
            public void onSuccess (DiffUtil.DiffResult result){
                result.dispatchUpdatesTo(mHistoryAdapter);
            }
        });
    }
}
