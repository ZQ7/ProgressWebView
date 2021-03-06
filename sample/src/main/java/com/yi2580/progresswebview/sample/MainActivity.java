package com.yi2580.progresswebview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import com.yi2580.progresswebview.OnWebViewLoad;
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

        //mWebView.loadUrl("https://sina.cn/");
        //mWebView.loadUrl("https://portal.3g.qq.com");
        mWebView.loadUrl("https://www.biyouxinjr.com/accredit/index.htm?params=/wx/open/index.html?user_channel=APPJZSH&mobile=e4eVczKEnozJeKNoY4SzS7uFrDInzxpPa4DnOC6DrdC0JhxGNZpi9a2QNi/sYYSwWdjpv9XR96TBXIvJ7VMyby+U9EM3buL/Kzduaso3sL5O+pn/zFoiouNG46wL7ze8UFujLcUADW1nBGwdasdbTwZJpohD6ZPZaL9mGvDJGYk=&user_terminal=1&sign=A171A12DDDE41E4A77F962897052A3FA");

        mWebView.setOnWebViewLoadListener(new OnWebViewLoad() {
            @Override
            protected void onProgressChanged(int progress) {
                super.onProgressChanged(progress);
            }

            @Override
            protected void onReceivedTitle(String title) {
                Log.e("MainActivity",title);
            }

            @Override
            protected boolean loadUrl(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        mWebView.setLoadingView(imageView);
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
