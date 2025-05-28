package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import c.plus.plan.skits.databinding.FragmentErrorBinding;

/**
 * @author Joey.zhao
 * @date 2025/4/21 11:12
 * @description 错误 Fragment
 */
public class ErrorFragment extends Fragment {

    private FragmentErrorBinding mBinding;
    private String titleText;
    private String msgText;
    public static ErrorFragment newInstance(String errorMsg,String errorCode) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("JOWO_ERROR_TITLE", errorMsg);
        bundle.putString("JOWO_ERROR_MSG", errorCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentErrorBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText = getArguments().getString("JOWO_ERROR_TITLE");
        msgText = getArguments().getString("JOWO_ERROR_MSG");

        initViews();
    }

    private void initViews() {

        mBinding.tvTitle.setText(titleText);
        mBinding.tvMsg.setText(msgText);
    }




}
