package com.raindragon.chapter8_simplewebbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.raindragon.chapter8_simplewebbrowser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val DEFAULT_HOME_URL = "http://www.google.com"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        bindingViews()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
//        http://www.google.com 로 접근시 기본 웹 브라우저를 실행 시킨다.
//        앱네 웹뷰 사용하려면 webViewClient 를 오버라이드 해줘야한다.
//        기본적으로 자바스크립트를 허용하지 않는다.
//        허용하기 위해서는
//        webView.settings.javaScriptEnabled = true
//        크로스 사이트 스크립팅(Xss)에 취약해 질수있다는 경고가 뜬다.

        binding.webView.apply {
            webViewClient = CustomWebViewClient()
            webChromeClient = CustomWebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_HOME_URL)
        }

    }

    private fun bindingViews() {
        binding.apply {
            etAddress.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val loadingUrl = v.text.toString()

//                    유알엘앞에 프로토콜이 있는가
                    if (URLUtil.isNetworkUrl(loadingUrl)) {
                        binding.webView.loadUrl(loadingUrl)
                    } else {
                        binding.webView.loadUrl("http://$loadingUrl")
                    }
                }
                false
            }

            btnBack.setOnClickListener {
                webView.goBack()
            }

            btnForward.setOnClickListener {
                webView.goForward()
            }

            btnHome.setOnClickListener {
                webView.loadUrl(DEFAULT_HOME_URL)
            }

            refreshLayout.setOnRefreshListener {
                webView.reload()
            }
        }
    }

//
//

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.pb.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.refreshLayout.isRefreshing = false
            binding.pb.hide()

            binding.btnBack.isEnabled = binding.webView.canGoBack()
            binding.btnForward.isEnabled = binding.webView.canGoForward()
            binding.etAddress.setText(url)
        }
    }

    inner class CustomWebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            binding.pb.progress = newProgress
        }
    }

}