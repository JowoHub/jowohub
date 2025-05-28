package c.plus.plan.skits.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.util.HashMap;
import java.util.Locale;

import c.plus.plan.skits.R;
import c.plus.plan.skits.constants.RouterHub;
import c.plus.plan.skits.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private String url;
    private ActivityWebViewBinding mBinding;

    public static void start(Context context, String url){
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(RouterHub.EXTRA_URL, url);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.back.setOnClickListener(v -> finish());


        mBinding.back.setOnClickListener(this);
        mBinding.btn.setOnClickListener(this);
        mBinding.web.addJavascriptInterface(new AndroidInterface(this), "jowo");
        mBinding.web.setWebChromeClient(new MyWebChromeClient());
        mBinding.web.setWebViewClient(new MyWebViewClient());

        mBinding.web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mBinding.web.getSettings().setJavaScriptEnabled(true);
        mBinding.web.getSettings().setAllowContentAccess(true);
        mBinding.web.getSettings().setDomStorageEnabled(true);

        Intent intent = getIntent();
        if(intent != null){
            String url = intent.getStringExtra(RouterHub.EXTRA_URL);
            if(!TextUtils.isEmpty(url)){
                loadUrl(url);
            }
        }
    }

    public class MyWebViewClient extends WebViewClient {

        // 覆盖 onPageFinished 方法
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 在这里处理网页加载完成的逻辑
            mBinding.loading.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtils.dTag("FWFW", "onPageStarted");
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
            LogUtils.dTag("FWFW", "onReceivedError");
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//            super.onReceivedHttpError(view, request, errorResponse);
            LogUtils.dTag("FWFW", "onReceivedHttpError", GsonUtils.toJson(errorResponse.getResponseHeaders()), errorResponse.getReasonPhrase(), errorResponse.getStatusCode());
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            LogUtils.dTag("FWFW", "onReceivedSslError");
        }
    }

    public class MyWebChromeClient extends WebChromeClient {

        // 覆盖 onJsAlert 方法
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            // 处理 alert 对话框
            // 这里可以显示自定义的对话框或者做其他处理
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 当点击确认按钮时，必须调用 result.confirm() 来处理结果
                            result.confirm();
                        }
                    })
                    .setCancelable(true)
                    .create()
                    .show();

            // 返回 true 表示你已经处理了这个 alert 事件
            return true;
        }
    }

    public class AndroidInterface {

        private Context context;

        public AndroidInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String getUserMsg() {
            // TODO 所有参数请换成开发者自己的参数，本demo只能演示用！！！
            // !!!!!!!!!!!!!!!!!!!!!!!!
            HashMap map = new HashMap();
            map.put("token", "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEsImNrZXkiOiJqeWk2eiIsIm91dFVpZCI6Im91dDAwMDEiLCJhcHBJZCI6IkpXMDAwMDAwIiwic2NvcGUiOlsiR1VFU1QiLCJBRE1JTiIsIlVTRVIiXSwiZXhwIjoyNTg5ODcwMDY5LCJwbGF0Zm9ybSI6ImFuZHJvaWQiLCJkYyI6InNnIn0.75DS7Xt3ZUk6ljzsgrfydvVPeAJM40Nrua6c6OLXbUk");
            map.put("country", LanguageUtils.getSystemLanguage().getCountry());
            map.put("language", Locale.getDefault().toLanguageTag());
            map.put("appId", "JW000000");
            map.put("key", "Ji1C1jn22d3KJRh4");
            map.put("iv", "6X4gBguAgMcpCfOs");
            return GsonUtils.toJson(map);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.back){
            finish();
        } else if(id == R.id.btn){
            loadUrl(mBinding.et.getText().toString());
        }

    }

    private void loadUrl(String url) {
        KeyboardUtils.hideSoftInput(mBinding.et);
        mBinding.loading.setVisibility(View.VISIBLE);
        mBinding.llInput.setVisibility(View.GONE);
        LogUtils.dTag("FWFW", url);
        mBinding.web.loadUrl(url);
    }
}
