package com.yi2580.progresswebview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by zhangqi on 2017/10/13.
 */

public class X5WebView extends FrameLayout {

    private static final String TAG = "X5WebView";

    //进度条View
    private ProgressView mProgressView;
    //WebView
    private WebView mWebView;
    private WebSettings mWebSettings;
    //WebViewClient类(处理各种通知 & 请求事件)
    private X5WebViewClient mWebViewClient;
    //WebChromeClient类(辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。)
    private X5WebChromeClient mWebChromeClient;

    private OnX5WebViewLoad onX5WebViewLoad;


    private boolean useProgress;//是否使用进度条
    private int progressColor;//进度条眼色
    private int progressHeight;//进度条高度

    private int currentProgress;

    private View placeholderView;

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressWebView);
        useProgress = ta.getBoolean(R.styleable.ProgressWebView_useProgress, true);
        progressColor = ta.getColor(R.styleable.ProgressWebView_progressColor, Color.RED);
        progressHeight = ta.getDimensionPixelSize(R.styleable.ProgressWebView_progressHeight, 5);
        ta.recycle();

        mWebView = new WebView(context, attrs);
        mWebView.setId(R.id.v_x5_webview);
        addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        if (useProgress) {
            mProgressView = new ProgressView(getContext());
            mProgressView.setId(R.id.v_x5_webview_progress);
            ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, progressHeight);
            mProgressView.setLayoutParams(mLayoutParams);
            mProgressView.setProgressColor(progressColor);
            addView(mProgressView);
        }

        mWebViewClient = new X5WebViewClient();
        mWebView.setWebViewClient(mWebViewClient);
        mWebChromeClient = new X5WebChromeClient();
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 设置WebView加载事件
     *
     * @param onWebViewLoad
     */
    public void setOnWebViewLoadListener(OnX5WebViewLoad onWebViewLoad) {
        if (onWebViewLoad == null) {
            return;
        }
        this.onX5WebViewLoad = onWebViewLoad;
    }

    /**
     * 返回前一个页面
     *
     * @return 是否消费返回事件
     */
    public boolean goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    /**
     * 第一次页面设置占位View
     *
     * @param view
     */
    public void setPlaceholderView(View view) {
        placeholderView = view;
    }

    /**
     * 设置自适应屏幕，两者合用
     */
    public void viewAdaption() {
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
    }

    /**
     * 缩放操作
     */
    public void viewZoom() {
        mWebSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
    }


    /**
     * 多窗口的问题
     */
    public void openNewWin() {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
    }

    /**
     * HTML5数据存储
     */
    public void openSaveData() {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    /**
     * 设置进度条颜色
     *
     * @param color
     */
    public void setProgressColor(int color) {
        if (mProgressView != null) {
            mProgressView.setProgressColor(color);
        }
    }

    /**
     * 设置进度条高度
     */
    public void setProgressHeight(int height) {
        if (mProgressView != null) {
            progressHeight = height;
            mProgressView.setProgressHeight(progressHeight);
        }
    }

    /**
     * 加载asset目录下网页文件
     *
     * @param url asset根目录下的文件名
     */
    public void loadAssetUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl("file:///android_asset/" + url);
        }
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    /**
     * 销毁WebView
     */
    public void destroy() {
        if (mWebView != null) {
            //mWebView.clearHistory();
            removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
        }
    }

    public void pauseTimers() {
        if (mWebView != null) {
            mWebView.pauseTimers();
        }
    }

    public void resumeTimers() {
        if (mWebView != null) {
            mWebView.resumeTimers();
        }
    }

    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    /**
     * WebViewClient
     */
    public class X5WebViewClient extends WebViewClient {
        boolean fristLoad = true;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
            //Log.e(TAG, "url : " + url);

            if (onX5WebViewLoad != null) {
                return onX5WebViewLoad.loadUrl(view, url);
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        /**
         * 当一个页面打开或者再次可见(从上一个页面返回)
         *
         * @param webView
         * @param s
         * @param bitmap
         */
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            //设定加载开始的操作
            currentProgress = 0;
            if (fristLoad && placeholderView != null) {
                fristLoad = false;
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                //layoutParams.setMargins(0, useProgress ? progressHeight : 0, 0, 0);
                addView(placeholderView, 1, layoutParams);
            }
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            //设定加载结束的操作
            if (placeholderView != null) {
                removeView(placeholderView);
                placeholderView = null;
            }
        }

        @Override
        public void onLoadResource(WebView webView, String s) {
            super.onLoadResource(webView, s);
            //设定加载资源的操作
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }


    /**
     * WebChromeClient
     */
    public class X5WebChromeClient extends WebChromeClient {

        //网页加载进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //Log.e(TAG, "newProgress == " + newProgress);
            //防止进度条在同一个页面来回跳
            if (newProgress > currentProgress) {
                currentProgress = newProgress;
                if (mProgressView != null && useProgress) {
                    mProgressView.setProgress(newProgress);
                }
                if (onX5WebViewLoad != null) {
                    onX5WebViewLoad.onProgressChanged(newProgress);
                }
            }

        }

        //网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {

            if (onX5WebViewLoad != null) {
                onX5WebViewLoad.onReceivedTitle(title);
            }
        }
    }


}
