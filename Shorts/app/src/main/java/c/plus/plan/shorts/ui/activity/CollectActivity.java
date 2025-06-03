package c.plus.plan.shorts.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.shorts.databinding.ActivityCollectBinding;
import c.plus.plan.shorts.ui.adapter.CollectAdapter;

public class CollectActivity extends BaseActivity {
    private CollectAdapter mCollectAdapter;
    private int mCursorId = 0;
    private final List<Drama> mOldList = new ArrayList<>();
    private final List<Drama> mNewList = new ArrayList<>();
    private boolean isLoad;
    private ActivityCollectBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCollectBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewList.isEmpty() || !isLoad){
            mBinding.srl.autoRefresh(0);
        }
    }
}
