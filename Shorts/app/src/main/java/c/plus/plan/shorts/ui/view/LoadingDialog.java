package c.plus.plan.shorts.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;

import c.plus.plan.common.constants.Constants;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.DialogLoadingBinding;

public class LoadingDialog   extends BaseDialogFragment {
    private static final String TAG = LoadingDialog.class.getSimpleName();
    private static LoadingDialog dialog;
    private DialogLoadingBinding mBinding;
    private boolean isShowBg = true;

    @Override
    protected int getLayout() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogLoadingBinding.bind(contentView);
    }

    public static void showLoading(Context context){
        showLoading(context, null, true);
    }

    public static void showLoading(Context context, boolean isShowBg){
        showLoading(context, null, isShowBg);
    }

    private static void showLoading(Context context, String msg, boolean isShowBg){
        dismissLoading();
        dialog = new LoadingDialog();
        dialog.isShowBg = isShowBg;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_DATA, msg);
        dialog.setArguments(bundle);
        dialog.show(context);
    }

    public static void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismissAllowingStateLoss();
            dialog = null;
        }
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
        if(isShowBg){
            return c.plus.plan.common.R.style.Dialog;
        } else {
            return c.plus.plan.common.R.style.DialogNoBg;
        }
    }
}
