package com.yi2580.progresswebview.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.tencent.smtt.sdk.WebView;
import com.yi2580.progresswebview.OnX5WebViewLoad;
import com.yi2580.progresswebview.X5WebView;

import static android.view.KeyEvent.KEYCODE_BACK;

public class X5WebViewActivity extends AppCompatActivity {

    private X5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x5_webview);

        mWebView = (X5WebView) findViewById(R.id.webview);


        //mWebView.loadUrl("https://sina.cn/");
        mWebView.openSaveData();
        mWebView.loadUrl("http://www.hulusaas.com/protocol");

        mWebView.setOnWebViewLoadListener(new OnX5WebViewLoad() {
            @Override
            protected void onProgressChanged(int progress) {
                super.onProgressChanged(progress);
            }

            @Override
            protected void onReceivedTitle(String title) {
                Log.e("MainActivity", title);
            }

            @Override
            protected boolean loadUrl(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.BLUE);
        imageView.setImageResource(R.mipmap.ic_launcher);
        mWebView.setPlaceholderView(imageView);
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
        mWebView.destroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK) {
            return mWebView.goBack();
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
