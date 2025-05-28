package c.plus.plan.skits.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.ViewUtils;

import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.skits.databinding.FragmentTabWebBinding;
import c.plus.plan.skits.ui.activity.WebViewActivity;
import c.plus.plan.skits.ui.adapter.BlockAndDramasAdapter;

public class TabWebFragment  extends Fragment {
    private FragmentTabWebBinding mBinding;
    private BlockAndDramasAdapter mBlockAndDramasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTabWebBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();

        mBinding.srl.autoRefresh();
        loadCache();
    }

    private void loadCache() {
        JOWOSdk.lastBlockDrama(result -> {
            if(result.getCode() == Result.Code.OK){
                ViewUtils.runOnUiThread(() -> {
                    mBlockAndDramasAdapter.setBlockAndDramasList(result.getData());
                    mBlockAndDramasAdapter.notifyDataSetChanged();
                });
            } else {
                // TODO 失败处理
            }
        });
    }

    private void initViews() {
        mBlockAndDramasAdapter = new BlockAndDramasAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rv.setLayoutManager(layoutManager);
        mBinding.rv.setAdapter(mBlockAndDramasAdapter);

        mBinding.srl.setOnRefreshListener(refreshLayout -> {
            fetchBlockAndDramasList();
        });

        mBlockAndDramasAdapter.setOnItemClickListener((position, item) -> {
//            JOWOSdk.playDrama(getContext(), item.getId(), item.getCover(), 0, 0);
//            https://www.jowo.tv/sdkplayer/?num=4&language=zh_cn&dramaId=102
            WebViewActivity.start(getContext(), "https://www.jowo.tv/sdkplayer/?num=1&language=" + LanguageUtils.getSystemLanguage().getLanguage() +"&dramaId=" + item.getJowoVid());
//            WebViewActivity.start(getContext(), "https://www.jowo.tv/sdkplayer/?num=4&language=zh_cn&dramaId=102");
        });
    }

    private void fetchBlockAndDramasList() {
        JOWOSdk.fetchBlockDrama(result -> {
            mBinding.srl.finishRefresh();
            if(result.getCode() == Result.Code.OK){
                mBlockAndDramasAdapter.setBlockAndDramasList(result.getData());
                mBlockAndDramasAdapter.notifyDataSetChanged();
            } else {
                // TODO 失败处理
            }
        });
    }
}
