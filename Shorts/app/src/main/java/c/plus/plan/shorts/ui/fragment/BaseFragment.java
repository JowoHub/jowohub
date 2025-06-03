package c.plus.plan.shorts.ui.fragment;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ShortsApplication;
import c.plus.plan.shorts.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {
    private ViewModelProvider mFragmentProvider;
    private ViewModelProvider mActivityProvider;
    private ViewModelProvider mApplicationProvider;
    private BaseActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }


    protected <T extends ViewModel> T getFragmentScopeViewModel(@NonNull Class<T> modelClass) {
        if (mFragmentProvider == null) {
            mFragmentProvider = new ViewModelProvider(this);
        }
        return mFragmentProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(mActivity);
        }
        return mActivityProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = new ViewModelProvider((ShortsApplication) mActivity.getApplicationContext());
        }
        return mApplicationProvider.get(modelClass);
    }

    public void showToast(int res){
        showToast(getResources().getString(res));
    }

    public void showToast(String toast){
        ToastUtils.make()
                .setBgResource(R.drawable.bg_toast)
                .setTextColor(getResources().getColor(R.color.white))
                .setTextSize(16)
                .setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(100))
                .show(toast);
    }
}
