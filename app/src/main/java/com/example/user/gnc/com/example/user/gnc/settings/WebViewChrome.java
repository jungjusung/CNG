package com.example.user.gnc.com.example.user.gnc.settings;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Jusung on 2016. 12. 19..
 */

public class WebViewChrome extends WebChromeClient{
    String title;
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (!TextUtils.isEmpty(title)) {
            this.title = title;
        }

    }
}
