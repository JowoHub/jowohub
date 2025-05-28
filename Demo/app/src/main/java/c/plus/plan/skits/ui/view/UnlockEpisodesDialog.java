package c.plus.plan.skits.ui.view;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.DialogUnlockEpisodesBinding;

public class UnlockEpisodesDialog extends BaseDialogFragment {
    private static final String TAG = UnlockEpisodesDialog.class.getSimpleName();
    private OnClickListener onClickListener;
    private DialogUnlockEpisodesBinding mBinding;
    private Drama mDrama;
    private Episodes mEpisodes;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_unlock_episodes;
    }

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        mBinding = DialogUnlockEpisodesBinding.bind(contentView);

        Bundle bundle = getArguments();
        String dramaJson = bundle.getString(RouterHub.EXTRA_DRAMA);
        String episodesJson = bundle.getString(RouterHub.EXTRA_EPISODES);
        try {
            Type type = new TypeToken<Drama>(){}.getType();
            mDrama = GsonUtils.fromJson(dramaJson, type);
            Type type1 = new TypeToken<Episodes>(){}.getType();
            mEpisodes = GsonUtils.fromJson(episodesJson, type1);
        } catch (JsonParseException e){
            e.printStackTrace();
        }

        String dramaInfo = mDrama.getJowoVid() + ":" + mDrama.getName();
        dramaInfo += "\n 第" + mEpisodes.getEpisodesNum() + "集";
        mBinding.drama.setText(dramaInfo);

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
        return true;
    }

    public interface OnClickListener {
        void unlock();
    }
}
