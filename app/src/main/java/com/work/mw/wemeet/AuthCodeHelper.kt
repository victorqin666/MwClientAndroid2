package com.work.mw.wemeet

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import com.mwim.qcloud.tim.uikit.TUIKit
import java.net.URLDecoder

typealias OnAuthCodeCallback = (authCode: String) -> Unit

object AuthCodeHelper {
    private const val TAG = "SLog"
//    private const val SAMPLE_SSO_URL_PREFIX = "https://demo4-idp.idaas.tencentcs.com/cidp/login/ai-b17a6f68b4ed47678c62e0e0a3fc3bb0?state=aHR0cHM6Ly9kZW1vNC1pZHAuaWRhYXMudGVuY2VudGNzLmNvbS9jaWRwL3Nzby9haS0xZGIxMzkwOGY5Njc0NTExOWUyYTg5YzVlYjJmNWUwYw==&id_token="
//    private const val SAMPLE_SSO_URL_PREFIX = "https://yzmetax-idp.id.meeting.qq.com/cidp/login/ai-2f96eed8349d4c7e8424cbe5a7136645?state=aHR0cHM6Ly95em1ldGF4LmlkLm1lZXRpbmcucXEuY29tL3Nzby9haS0xZTJlMzA5NjVhZjE0OGM3YWY5ODhjNGY3NzA3YTdlNg==&id_token="
    private val UI_HANDLER by lazy { Handler(Looper.getMainLooper()) }

    @JvmStatic
    fun getAuthCode(url: String, callback: OnAuthCodeCallback) {
        UI_HANDLER.post {
            val webView = WebView(TUIKit.getAppContext())
            val webSetting = webView.settings
            webSetting.javaScriptEnabled = true
            webSetting.domStorageEnabled = true
            // 不使用缓存
            webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
            webSetting.setAppCacheEnabled(false)
            webSetting.allowFileAccess = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webSetting.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
            webView.webViewClient = object : WebViewClient() {
                private var hasParsedParam = false

                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    processUrlIfNecessary(url)
                    return super.shouldOverrideUrlLoading(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    // 部分机型 走一遍之后不加载加载js了，再load一下
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webView.loadUrl(request?.url.toString())
                    } else {
                        webView.loadUrl(request.toString())
                    }
                    return false
                }

                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                    handleError(errorCode)
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }

                @TargetApi(Build.VERSION_CODES.M)
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    handleError(error?.errorCode)
                    super.onReceivedError(view, request, error)
                }

                override fun onPageFinished(view: WebView?, url: String) {
                    super.onPageFinished(view, url)
                    processUrlIfNecessary(url)
                }

                private fun processUrlIfNecessary(url: String) {
                    val isTargetLink = url.contains("redirect_uri") && url.contains("sso_auth_code")
                    if (!isTargetLink) {
                        Log.i(TAG, "$url is not target link.")
                        return
                    }
                    if (hasParsedParam) {
                        Log.i(TAG, "already parsed.")
                        return
                    }
                    val authCode = Uri.parse(url).getQueryParameter("redirect_uri")?.let { uri ->
                        Uri.parse(URLDecoder.decode(uri, "utf-8")).getQueryParameter("sso_auth_code")
                    } ?: ""
                    Log.i(TAG, "auth code = $authCode")
                    hasParsedParam = true
                    UI_HANDLER.post { callback.invoke(authCode) }
                }

                private fun handleError(errorCode: Int?) {
                    if (errorCode != ERROR_UNSUPPORTED_SCHEME) {
                        Log.i(TAG, "get auth code failed")
                        hasParsedParam = true
                        UI_HANDLER.post { callback.invoke("") }
                    }
                }
            }
            webView.loadUrl(url)
            Log.i(TAG, " webView.loadUrl(url) $url")
        }
    }
}
