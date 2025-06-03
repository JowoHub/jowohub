package c.plus.plan.shorts.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

import c.plus.plan.jowosdk.entity.Feedback;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Result;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.contants.RouterHub;
import c.plus.plan.shorts.databinding.ActivityDramaEpisodesBinding;
import c.plus.plan.shorts.databinding.ActivityFeedbackBinding;
import c.plus.plan.shorts.ui.adapter.FeedbackOptionAdapter;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFeedbackBinding mBinding;
    private Feedback mFeedback;
    private FeedbackOptionAdapter mAdapter;

    public static void start(Context context, Feedback feedback){
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.putExtra(RouterHub.EXTRA_FEEDBACK, feedback);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initParams();
        initViews();
    }

    private void initViews() {
        mAdapter = new FeedbackOptionAdapter();
        List<String> options = new ArrayList<>();
        options.add(getResources().getString(R.string.report_option1));
        options.add(getResources().getString(R.string.report_option2));
        options.add(getResources().getString(R.string.report_option3));
        options.add(getResources().getString(R.string.report_option4));
        mAdapter.setOptions(options);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mBinding.rv.setLayoutManager(layoutManager);
        mBinding.rv.setAdapter(mAdapter);

        mBinding.back.setOnClickListener(this);
        mBinding.submit.setOnClickListener(this);
    }

    private void initParams() {
        Intent intent = getIntent();
        mFeedback = intent.getParcelableExtra(RouterHub.EXTRA_FEEDBACK);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.back){
            finish();
        } else if(id == R.id.submit){
            submit();
        }
    }

    private void submit() {
        String option = mAdapter.getCurrentOption();
        if(TextUtils.isEmpty(option)){
            showToast(R.string.report_option_empty);
            return;
        }

        String email = mBinding.email.getText().toString();
        if(TextUtils.isEmpty(email)){
            showToast(R.string.report_email_empty);
            return;
        }

        mFeedback.setTitle(option);
        mFeedback.setEmail(email);
        mFeedback.setContent(mBinding.other.getText().toString());

        JOWOSdk.postFeedback(mFeedback, new Callback() {
            @Override
            public void result(Result result) {
                if(result.getCode() == Result.Code.OK){
                    showToast(R.string.report_submit_suc);
                    finish();
                } else {
                    showToast(R.string.report_submit_fail);
                }
            }
        });
    }

    @Override
    public void finish() {
        KeyboardUtils.hideSoftInput(mBinding.other);
        KeyboardUtils.hideSoftInput(mBinding.email);
        super.finish();
    }
}
