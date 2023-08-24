package com.funny.fairGo.models

import android.content.Context
import android.webkit.WebView

class WebViewProvider(private val context: Context) {
    private var webView: WebView? = null

    fun getInstance(): WebView {
        if(webView == null) {
            webView = WebView(context)
        }
        return webView ?: WebView(context)
    }
}