package com.yi2580.progresswebview;

import com.tencent.smtt.sdk.WebView;

/**
 * Created by zhangqi on 2017/8/28.
 */

public abstract class OnX5WebViewLoad {
    /**
     * 进度
     *
     * @param progress
     */
    protected void onProgressChanged(int progress) {
    }

    /**
     * 网页Title
     *
     * @param title
     */
    protected void onReceivedTitle(String title) {
    }

    /**
     * 加载URL
     *
     * @param view
     * @param url
     * @return
     */
    protected abstract boolean loadUrl(WebView view, String url);

}
