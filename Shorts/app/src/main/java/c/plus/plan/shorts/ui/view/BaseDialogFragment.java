package c.plus.plan.shorts.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.LogUtils;

import java.util.Objects;


/**
 * Created by fanwei on 2/1/22
 */
public abstract class BaseDialogFragment extends DialogFragment {
    private View mContentView;

    private DialogDismissListener mDialogDismissListener;

    private static final float DEFAULT_SCALE = 0.9f;
    private float mScale = DEFAULT_SCALE;

    /**
     * @return dialog对应初始化视图
     */
    protected abstract int getLayout();

    /**
     * dialog初始化方法
     */
    protected abstract void init(Bundle savedInstanceState, View contentView);

    /**
     * 用于获取该tag对应对话框
     */
    protected abstract String getDialogTag();


    /**
     * @return 物理返回键回调
     */
    protected abstract boolean onKeyBack();

    /**
     * @return 对话框以外的地方是否可以点击取消对话框
     */
    protected abstract boolean getCanCancelOutSide();

    protected abstract int getDialogStyle();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog mDialog = new Dialog(getActivity(), getDialogStyle());
        mContentView = LayoutInflater.from(getActivity()).inflate(getLayout(), null);
        mDialog.setContentView(mContentView);
        mDialog.setCanceledOnTouchOutside(getCanCancelOutSide());// 默认触摸对话框以外的地方不可取消对话框
        mDialog.setOnDismissListener(dialog -> {
            LogUtils.dTag("DialogFragment", "dialog onDismiss");
            if (mDialogDismissListener != null) {
                mDialogDismissListener.dismissAction();
            }
        });

        return mDialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getDialog()).setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                return onKeyBack();
            }
            return false;
        });

        init(savedInstanceState, mContentView);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOpenDefault()) {
            setDefaultProperty();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCloseDialog()) { // 解决home键或锁屏是否需要关闭dialog
            if (getDialog() != null && getDialog().isShowing()) {
                getDialog().dismiss();
            }
        }
    }

    public boolean isCloseDialog() {
        return false;
    }

    /**
     * @return 对话框占屏幕比例，默认全填充
     */
    protected boolean isOpenDefault() {
        return true;
    }

    public void setDefaultProperty() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (getDialog() == null) {
                return;
            }
            WindowManager.LayoutParams params = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
            Display display =
                    ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay();
            params.width = (int) (display.getWidth() * mScale);
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
    }

    public void setWidthScale(float scale){
        mScale = scale;
    }

    /**
     * @return 该对话是否正在展示
     */
    public boolean isShowing() {
        if (getActivity() == null) {
            return false;
        }
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(getDialogTag());
        if (null != fragment) {
            return true;
        }
        return false;
    }

    /**
     * 通过fragmentManager展示对话框，避免默认commit方法stateLoss等问题
     *
     * @param context 后期对于拿不到fm的调用地方，需要获取当前activity方法，从而扩展方法
     */
    public void show(Context context) {
        if (!isAdded() && context instanceof FragmentActivity) {
            try {
                String tag = getDialogTag();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment != null) {
                    ft.remove(fragment);
                }
                ft.add(this, tag);
                ft.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDialogDismissListener(DialogDismissListener dialogDismissListener) {
        mDialogDismissListener = dialogDismissListener;
    }

    public interface DialogDismissListener {
        void dismissAction();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        if (mDialogDismissListener != null) {
            mDialogDismissListener.dismissAction();
        }
        super.onDestroy();
    }
}
