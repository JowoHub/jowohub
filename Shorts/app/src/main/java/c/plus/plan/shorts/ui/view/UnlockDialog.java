package c.plus.plan.shorts.ui.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ThreadUtils;

import c.plus.plan.common.entity.Current;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.DialogUnlockBinding;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.manager.ShortsDataBase;
import c.plus.plan.shorts.ui.fragment.VipFragment;

public class UnlockDialog extends BaseDialogFragment {
    private static final String TAG = MenuDialog.class.getSimpleName();
    private OnClickListener onClickListener;
    private DialogUnlockBinding mBinding;
    private int dramaId;
    private int episodesNum;
    private DramaNumUnlock dramaNumUnlock;
    private VipFragment vipFragment;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_unlock;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogUnlockBinding.bind(contentView);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ScreenUtils.getScreenWidth();
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);

        Bundle bundle = getArguments();
        dramaId = bundle.getInt(RouterHub.EXTRA_DRAMA_ID);
        episodesNum = bundle.getInt(RouterHub.EXTRA_EPISODES_NUM);

        ShortsDataBase.dbWriteExecutor.execute(() -> {
            dramaNumUnlock = ShortsDataBase.get().dramaNumUnlockDao().select(dramaId, episodesNum);
            if(dramaNumUnlock == null){
                dramaNumUnlock = new DramaNumUnlock();
                dramaNumUnlock.setDramaId(dramaId);
                dramaNumUnlock.setEpisodesNum(episodesNum);
            }
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateViews();
                }
            });
        });

        int coinCount = Current.getCoin();
        mBinding.coinCount.setText(getResources().getString(R.string.balance) + ": " + coinCount + getResources().getString(R.string.coin));

        mBinding.close.setOnClickListener(v -> dismiss());
        mBinding.unlock.setOnClickListener(v -> {
            if(onClickListener != null){
                onClickListener.unlock(dramaNumUnlock);
            }
        });

        if(savedInstanceState == null){
            vipFragment = new VipFragment();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.vip, vipFragment)
                    .commit();
            vipFragment.setOnClickListener(new VipFragment.OnClickListener() {
                @Override
                public void vip(boolean suc) {
                    if(suc && onClickListener != null){
                        onClickListener.vip(suc);
                    }
                }

                @Override
                public void topUp() {
                    if(onClickListener != null){
                        onClickListener.topUp();
                    }
                }
            });
        }
    }

    public int adCountPlus(){
        dramaNumUnlock.setAdCount(dramaNumUnlock.getAdCount() + 1);
        updateViews();
        ShortsDataBase.dbWriteExecutor.execute(() -> {
            ShortsDataBase.get().dramaNumUnlockDao().insert(dramaNumUnlock);
        });
        if(dramaNumUnlock.getAdCount() >= DramaNumUnlock.getUnlockAdCount()){
            dismissAllowingStateLoss();
        }
        return dramaNumUnlock.getAdCount();
    }

    private void updateViews() {
        mBinding.count.setText(dramaNumUnlock.getAdCount() + "/" + DramaNumUnlock.getUnlockAdCount());
        mBinding.pb.setProgress((int) (Float.valueOf(dramaNumUnlock.getAdCount())*100/DramaNumUnlock.getUnlockAdCount()));
        mBinding.adDesc.setText(String.format(getResources().getString(R.string.unlock_ad_count), DramaNumUnlock.getUnlockAdCount() - dramaNumUnlock.getAdCount()));
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
        return false;
    }

    public interface OnClickListener {
        void unlock(DramaNumUnlock dramaNumUnlock);
        void vip(boolean suc);
        void topUp();
    }
}
