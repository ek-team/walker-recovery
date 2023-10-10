package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.pharos.walker.R;
import com.pharos.walker.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/5/31
 * Describe:
 */
public class NewsActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.webview)
    WebView webview;
    //    private String url = "https://eshineinfo.com/jiqiren/public/cms/a/33";//36
    private String url = "http://pharos.ewj100.com/manage.html#/product/show/index";//36
//    private String url = "https://pharos.ewj100.com/record.html#/ucenter/recovery/recoveryInfo?idCard=41302619990823002X";//36
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initWebView();
        webview.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSetting = webview.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        if(NetworkUtils.isConnected()){
            webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else {
//            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSetting.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
//        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webview.loadUrl(url);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_news;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
