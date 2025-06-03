package c.plus.plan.shorts.ui.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;

import c.plus.plan.common.entity.Current;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.DialogCoinUnlockBinding;
import c.plus.plan.shorts.entity.DramaPrice;

public class CoinUnlockDialog extends BaseDialogFragment {
    private static final String TAG = MenuDialog.class.getSimpleName();
    private OnClickListener onClickListener;
    private DialogCoinUnlockBinding mBinding;
    private int dramaCoin;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_coin_unlock;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogCoinUnlockBinding.bind(contentView);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ScreenUtils.getScreenWidth();
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);

        Bundle bundle = getArguments();
        dramaCoin = bundle.getInt(RouterHub.EXTRA_DRAMA_COIN);

        int coinCount = Current.getCoin();
        mBinding.coinCount.setText(getResources().getString(R.string.balance) + ": " + coinCount + getResources().getString(R.string.coin));
        mBinding.coin.setText(String.valueOf(dramaCoin));

        mBinding.close.setOnClickListener(v -> dismiss());
        mBinding.btn.setOnClickListener(v -> {
            if(onClickListener != null){
                onClickListener.unlock();
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
        return false;
    }

    public interface OnClickListener {
        void unlock();
    }
}
