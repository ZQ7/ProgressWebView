package com.yi2580.progresswebview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

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
//        mWebView.viewAdaption();
//        mWebView.viewZoom();
//        mWebView.openOtherSetting();
        mWebView.openNewWin();
        mWebView.openSaveData();

        mWebView.loadUrl("https://sina.cn/");
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers(); //小心这个！！！暂停整个 WebView 所有布局、解析、JS。
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.mDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK){
            return mWebView.mGoBack();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_webview:
                startActivity(new Intent(this, WebViewInCodeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
