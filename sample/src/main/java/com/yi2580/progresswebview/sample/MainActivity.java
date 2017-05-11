package com.yi2580.progresswebview.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.yi2580.progresswebview.ProgressWebView;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {

    private ProgressWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (ProgressWebView) findViewById(R.id.main_webview);

        mWebView.openJavaScript();
//        mProgressWebView.viewAdaption();
//        mProgressWebView.viewZoom();
//        mProgressWebView.openOtherSetting();
//        mProgressWebView.openNewWin();
        mWebView.openSaveData();

        mWebView.loadUrl("https://sina.cn/");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK){
            return mWebView.mGoBack();
        }
        return super.onKeyUp(keyCode, event);
    }
}
