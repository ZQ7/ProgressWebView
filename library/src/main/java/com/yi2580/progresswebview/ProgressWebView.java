package com.yi2580.progresswebview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by zhangqi on 2017/5/9.
 * A webview with a progress bar
 */

public class ProgressWebView extends WebView {

    //进度条View
    private ProgressView mProgressView;
    //WebView
    //private WebView mWebView;
    private WebSettings mWebSettings;
    //WebViewClient类(处理各种通知 & 请求事件)
    private ProgressWebViewClient mWebViewClient;
    //WebChromeClient类(辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。)
    private ProgressWebChromeClient mWebChromeClient;

    private OnWebViewLoad onWebViewLoad;

    private View loadingView;

    private boolean useProgress;//是否使用进度条
    private int progressColor;//进度条眼色
    private int progressHeight;//进度条高度

    public ProgressWebView(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressWebView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        init(context, attrs);

    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressWebView);
        useProgress = ta.getBoolean(R.styleable.ProgressWebView_useProgress, true);
        progressColor = ta.getColor(R.styleable.ProgressWebView_progressColor, Color.RED);
        progressHeight = ta.getDimensionPixelSize(R.styleable.ProgressWebView_progressHeight, 5);
        ta.recycle();

        mWebViewClient = new ProgressWebViewClient();
        this.setWebViewClient(mWebViewClient);
        mWebChromeClient = new ProgressWebChromeClient();
        this.setWebChromeClient(mWebChromeClient);
        mWebSettings = this.getSettings();
        createViews();
    }

    /**
     * 设置WebView加载事件
     *
     * @param onWebViewLoad
     */
    public void setOnWebViewLoadListener(OnWebViewLoad onWebViewLoad) {
        if (onWebViewLoad == null) {
            return;
        }
        this.onWebViewLoad = onWebViewLoad;
    }

    public void setLoadingView(View view) {
        this.loadingView = view;
    }

    /**
     * When create ProgressWebView not in XML,Call this method.
     *
     * @param height
     * @param color
     */
    public void useProgress(int height, int color) {
        useProgress = true;
        progressHeight = height;
        progressColor = color;
        if (mProgressView == null) {
            createViews();
        } else {
            setProgressHeight(progressHeight);
            mProgressView.setProgressColor(color);
        }
    }

    /**
     * 是否使用进度条
     *
     * @param isUsed
     */
    public void isUseProgress(boolean isUsed) {
        useProgress = isUsed;
        if (isUsed) {
            if (mProgressView == null) {
                createViews();
            }
        } else {
            if (mProgressView != null) {
                mProgressView.setVisibility(View.INVISIBLE);
            }
        }
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
     * 调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
     */
    public void openJavaScript() {
        mWebSettings.setJavaScriptEnabled(true);
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
     * 其他细节操作
     */
    public void openOtherSetting() {
        //其他细节操作
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    /**
     * 多窗口的问题
     */
    public void openNewWin() {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(false);
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
     * 渲染完成时初始化view(仅限xml)
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 初始化viwe
     */
    private void createViews() {
        Log.e("ProgressWebView", "CreateWebView");
        if (useProgress && mProgressView == null) {
            mProgressView = new ProgressView(getContext());
            ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, progressHeight);
            mProgressView.setLayoutParams(mLayoutParams);
            mProgressView.setProgressColor(progressColor);
            addView(mProgressView);
        }

    }


    /**
     * 加载asset目录下网页文件
     *
     * @param url asset根目录下的文件名
     */
    public void loadAssetUrl(String url) {
        loadUrl("file:///android_asset/" + url);
    }


    public ProgressWebViewClient getProgressWebViewClient() {
        return mWebViewClient;
    }


    public void setProgressWebViewClient(ProgressWebViewClient mWebViewClient) {
        this.mWebViewClient = mWebViewClient;
    }

    public ProgressWebChromeClient getProgressWebChromeClient() {
        return mWebChromeClient;
    }

    public void setProgressWebChromeClient(ProgressWebChromeClient mWebChromeClient) {
        this.mWebChromeClient = mWebChromeClient;
    }

    /**
     * 返回前一个页面
     *
     * @return 是否消费返回事件
     */
    public boolean mGoBack() {
        if (this.canGoBack()) {
            this.goBack();
            return true;
        }
        return false;
    }

    /**
     * 销毁WebView
     */
    public void mDestroy() {
        if (this != null) {
            this.clearHistory();
            ((ViewGroup) getParent()).removeView(this);
            this.loadUrl("about:blank");
            this.stopLoading();
            this.setWebChromeClient(null);
            this.setWebViewClient(null);
            this.destroy();
        }
    }

    /**
     * WebViewClient
     */
    public class ProgressWebViewClient extends WebViewClient {
        boolean frist = true;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
            Log.e("ProgressWebView", "url : " + url);

            if (onWebViewLoad != null) {
                return onWebViewLoad.loadUrl(view, url);
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);

        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (frist && loadingView != null) {
                frist = false;
                int marginTop = 0;
                if (useProgress) {
                    marginTop = progressHeight;
                }
                ViewGroup.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, marginTop);
                if (loadingView.getParent() != null) {
                    ((ProgressWebView) loadingView.getParent()).removeView(loadingView);
                }
                view.addView(loadingView, layoutParams);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //设定加载结束的操作
            if (loadingView != null) {
                view.removeView(loadingView);
                loadingView = null;
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //设定加载资源的操作
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }


        //webView默认是不处理https请求的，页面显示空白，需要进行如下设置：
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();    //表示等待证书响应
            // handler.cancel();      //表示挂起连接，为默认方式
            // handler.handleMessage(null);    //可做其他处理
        }
    }


    /**
     * WebChromeClient
     */
    public class ProgressWebChromeClient extends WebChromeClient {
        //网页加载进度
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //Log.e("ProgressWebView", "Progress == " + newProgress);
            if (mProgressView != null && useProgress) {
                mProgressView.setProgress(newProgress);
            }
            if (onWebViewLoad != null) {
                onWebViewLoad.onProgressChanged(newProgress);
            }
        }

        //网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            //Log.e("ProgressWebView", "title == " + title);
            if (onWebViewLoad != null) {
                onWebViewLoad.onReceivedTitle(title);
            }
        }

        //多窗口的问题
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebViewTransport transport = (WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================
    }
}
