package c.plus.plan.skits.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.text.TextUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdkui.DramaPlayerUiFragment;
import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.ActivityMainBinding;
import c.plus.plan.skits.ui.adapter.MainViewPagerAdapter;
import c.plus.plan.skits.ui.fragment.TabForUFragment;
import c.plus.plan.skits.ui.view.EpisodesListDialog;
import c.plus.plan.skits.ui.view.UnlockEpisodesDialog;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private MainViewPagerAdapter mViewPager;
    private List<String> mFragments = new ArrayList<>();
    private TabLayoutMediator mediator;
    private String currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewPager = new MainViewPagerAdapter(this);
        mFragments.add(RouterHub.FRAGMENT_TAB_MAIN);
        mFragments.add(RouterHub.FRAGMENT_TAB_BLOCK);
        mFragments.add(RouterHub.FRAGMENT_TAB_LABEL);
        mFragments.add(RouterHub.FRAGMENT_TAB_FOR_U);
        mFragments.add(RouterHub.FRAGMENT_TAB_API);
//        mFragments.add(RouterHub.FRAGMENT_TAB_H5);
        mViewPager.setFragmentNames(mFragments);

        mBinding.pager.setAdapter(mViewPager);
        mBinding.pager.setUserInputEnabled(false);

        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentFragment = mFragments.get(position);
                if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_API)){
                    Intent intent = new Intent(MainActivity.this, DataApiActivity.class);
                    MainActivity.this.startActivity(intent);
                } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_FOR_U)){
                    TabForUFragment currentFragment = (TabForUFragment) getSupportFragmentManager().findFragmentByTag("f" + mViewPager.getItemId(position));
                    if(currentFragment != null){
                        currentFragment.playCurrent();
                    }
                }
//                else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_H5)){
//                    WebViewActivity.start(MainActivity.this, "");
//                }
            }
        });

        mediator = new TabLayoutMediator(mBinding.tab, mBinding.pager, false, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getFragmentName(position));
            }
        });
        mediator.attach();

        // 创建 OnBackPressedCallback 并定义处理逻辑
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 在此处定义按下返回按钮时要执行的操作
                // 例如，显示一个确认退出对话框
                back();
            }
        };

        // 将回调添加到 OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

//        JOWOSdk.registerOnPlayerListener(onPlayerListener);
//        JOWOSdk.registerOnShowListener(onShowListener);
    }

    @Override
    protected void onDestroy() {
//        JOWOSdk.unRegisterOnPlayerListener(onPlayerListener);
//        JOWOSdk.unRegisterOnShowListener(onShowListener);
        super.onDestroy();
    }

//    private final Listener.OnShowListener onShowListener = new Listener.OnShowListener(){
//
//        @Override
//        public void showEpisodesList(Context context, Drama drama, int num) {
//            showEpisodesListDialog(context, drama);
//        }
//
//        @Override
//        public void showUnlock(Context context, Drama drama, Episodes episodes) {
//            showUnlockDialog(context, drama, episodes);
//        }
//
//        @Override
//        public void showUnLockResult(Context context, int dramaId, int num, boolean success) {
//            if(!success){
//                ToastUtils.showLong(R.string.unlock_fail);
//            }
//        }
//
//        @Override
//        public void showCollectResult(Context context, boolean success) {
//            LogUtils.dTag("FWFW",   "收藏： " + success);
//        }
//
//        @Override
//        public void showCancelCollectResult(Context context, boolean success) {
//            LogUtils.dTag("FWFW",   "取消收藏： " + success);
//        }
//
//        @Override
//        public void showMenu(Context context, Drama drama, int num) {
//
//        }
//    };
//
//    private final Listener.OnPlayerListener onPlayerListener = new Listener.OnPlayerListener() {
//        @Override
//        public void start(Drama drama, Episodes episodes) {
//
//        }
//
//        @Override
//        public void pause(Drama drama, Episodes episodes, long currentPosition) {
//
//        }
//
//        @Override
//        public void finish(Drama drama, Episodes episodes) {
//
//        }
//
//        @Override
//        public void error(int id, int num, String msg) {
//
//        }
//
//        @Override
//        public void controllerClick(int action) {
//
//        }
//    };

//    private void showEpisodesListDialog(Context context, Drama drama) {
//        EpisodesListDialog dialog = new EpisodesListDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString(RouterHub.EXTRA_DRAMA, GsonUtils.toJson(drama));
//        dialog.setArguments(bundle);
//        dialog.setOnClickListener((num, isLock) -> {
//            if(isLock){
//                dialog.dismiss();
//                Episodes episodes = new Episodes();
//                episodes.setEpisodesNum(num);
//                showUnlockDialog(context, drama, episodes);
//            } else {
//                dialog.dismiss();
//                JOWOSdk.playDrama(this, drama.getId(), drama.getCover(), num, 0);
//            }
//        });
//        dialog.show(context);
//    }
//
    public String getCurrentFragment() {
        return currentFragment;
    }
//
//    private void showUnlockDialog(Context context, Drama drama, Episodes episodes) {
//        UnlockEpisodesDialog dialog = new UnlockEpisodesDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString(RouterHub.EXTRA_DRAMA, GsonUtils.toJson(drama));
//        bundle.putString(RouterHub.EXTRA_EPISODES, GsonUtils.toJson(episodes));
//        dialog.setArguments(bundle);
//        dialog.setOnClickListener(() -> {
//            dialog.dismiss();
//            JOWOSdk.unlockEpisodes(this, drama.getId(), episodes.getEpisodesNum(), LanguageUtils.getSystemLanguage().getLanguage());
//        });
//
//        dialog.show(context);
//    }

    private void back() {
        ActivityUtils.startHomeActivity();
    }

    private String getFragmentName(int position) {
        if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_MAIN)){
            return getResources().getString(R.string.tab_main);
        } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_BLOCK)){
            return getResources().getString(R.string.tab_block);
        } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_LABEL)){
            return getResources().getString(R.string.tab_label);
        } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_API)){
            return getResources().getString(R.string.tab_api);
        } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_FOR_U)){
            return getResources().getString(R.string.tab_for_u);
        } else if(TextUtils.equals(mFragments.get(position), RouterHub.FRAGMENT_TAB_H5)){
            return getResources().getString(R.string.tab_h5);
        }

        return null;
    }
}
