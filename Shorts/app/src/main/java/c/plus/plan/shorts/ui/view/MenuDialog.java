package c.plus.plan.shorts.ui.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.DialogMenuBinding;
import c.plus.plan.shorts.ui.adapter.MenuAdapter;
import c.plus.plan.shorts.ui.entity.Menu;

public class MenuDialog extends BaseDialogFragment {
    private static final String TAG = MenuDialog.class.getSimpleName();
    private OnClickListener onClickListener;
    private DialogMenuBinding mBinding;
    private ArrayList<Menu> mMenuList;
    private MenuAdapter mAdapter;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_menu;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogMenuBinding.bind(contentView);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ScreenUtils.getScreenWidth();
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);

        Bundle bundle = getArguments();
        mMenuList = bundle.getParcelableArrayList(RouterHub.EXTRA_MENU);
        mAdapter = new MenuAdapter();
        mAdapter.setMenus(mMenuList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setLayoutManager(layoutManager);
        mBinding.rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Menu item) {
                if(onClickListener != null){
                    onClickListener.click(item);
                }
            }
        });

        mBinding.cancel.setOnClickListener(v -> dismiss());
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
        void click(Menu menu);
    }
}
