package com.yi2580.progresswebview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yi2580.progresswebview.OnWebViewLoad;
import com.yi2580.progresswebview.ProgressWebView;

/**
 * Created by zhangqi on 2017/5/12.
 */

public class WebViewInCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout webViewLayout;
    private ProgressWebView mWebView;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_in_code);

        initView();
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
        webViewLayout = (LinearLayout) findViewById(R.id.layout_webview);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new ProgressWebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        //mWebView.useProgress(20, Color.BLACK);
        //mWebView.setProgressHeight();
        webViewLayout.addView(mWebView);

        mWebView.openJavaScript();
        mWebView.openNewWin();
        mWebView.openSaveData();
        mWebView.openOtherSetting();
        //mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://hz.m.sohu.com/");
        mWebView.setOnWebViewLoadListener(new OnWebViewLoad() {
            @Override
            protected void onProgressChanged(int progress) {
                super.onProgressChanged(progress);
            }

            @Override
            protected void onReceivedTitle(String title) {
                Log.e("WebViewInCodeActivity", title);
            }

            @Override
            protected boolean loadUrl(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        mWebView.setProgressHeight(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.mDestroy();
    }

}
