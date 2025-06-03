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
import c.plus.plan.shorts.databinding.DialogCustomerBinding;
import c.plus.plan.shorts.databinding.DialogUnlockBinding;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.manager.ShortsDataBase;
import c.plus.plan.shorts.ui.fragment.VipFragment;

public class CustomerDialog  extends BaseDialogFragment {
    private static final String TAG = "CustomerDialog";
    private DialogCustomerBinding mBinding;

    @Override
    protected int getLayout() {
        return R.layout.dialog_customer;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogCustomerBinding.bind(contentView);

        mBinding.close.setOnClickListener(v -> dismiss());
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
}
