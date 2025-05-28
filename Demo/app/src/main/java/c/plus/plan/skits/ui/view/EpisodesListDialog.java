package c.plus.plan.skits.ui.view;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.DialogEpisodesListBinding;
import c.plus.plan.skits.ui.adapter.EpisodesAdapter;

public class EpisodesListDialog extends BaseDialogFragment {
    private static final String TAG = UnlockEpisodesDialog.class.getSimpleName();
    private OnClickListener onClickListener;
    private DialogEpisodesListBinding mBinding;
    private Drama mDrama;
    private EpisodesAdapter mAdapter;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_episodes_list;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogEpisodesListBinding.bind(contentView);

        Bundle bundle = getArguments();
        String dramaJson = bundle.getString(RouterHub.EXTRA_DRAMA);
        try {
            Type type = new TypeToken<Drama>(){}.getType();
            mDrama = GsonUtils.fromJson(dramaJson, type);
        } catch (JsonParseException e){
            e.printStackTrace();
        }

        String dramaInfo = mDrama.getJowoVid() + ":" + mDrama.getName();
        mBinding.drama.setText(dramaInfo);

        mAdapter = new EpisodesAdapter();
        mAdapter.setEpisodeCount(mDrama.getEpisodesCount());
        mAdapter.setFreeCount(mDrama.getFreeEpisodesCount());
        int spanCount = (ScreenUtils.getScreenWidth())/ SizeUtils.dp2px(65);
        int spacing = SizeUtils.dp2px(6); // 间距
        boolean includeEdge = true; // 是否包括边缘
        mBinding.rv.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mBinding.rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mBinding.rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new EpisodesAdapter.OnItemClickListener() {
            @Override
            public void click(int num, boolean isLock) {
                if(onClickListener != null){
                    onClickListener.click(num, isLock);
                }
            }
        });

        // 建议使用fetchUserDrama获取最新的解锁剧集信息！！！demo是播放页已请求
        JOWOSdk.lastUserDrama(mDrama.getJowoVid(), result -> {
            if(result.getData() != null){
                mAdapter.setUnlockNums(result.getData().getUnlockEpisodesNum());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected String getDialogTag() {
        return TAG;
    }

    @Override
    protected boolean onKeyBack() {
        return false;
    }

    @Override
    protected boolean getCanCancelOutSide() {
        return true;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.Dialog;
    }

    @Override
    protected boolean isOpenDefault() {
        return true;
    }

    public interface OnClickListener {
        void click(int num, boolean isLock);
    }
}
