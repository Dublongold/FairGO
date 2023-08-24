package com.funny.fairGo


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.funny.fairGo.extentions.toArray
import com.funny.fairGo.models.ImportantCallback
import com.funny.fairGo.models.WebViewProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class WebWindow : AppCompatActivity() {
    private lateinit var webViewProvider: WebViewProvider
    private var callback: Uri? = null
    private lateinit var mainUrl: String

    private fun getNecessaryId(): Int = R.layout.web_window

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getNecessaryId())
        mainUrl = (application as GlobalApplication).url
        webViewProvider = WebViewProvider(this)
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(webViewProvider.getInstance().canGoBack()) {
                    webViewProvider.getInstance().goBack()
                }
            }
        })
        setPropertiesForWebView()
    }

    private fun setPropertiesForWebView() {
        setValueForSettings()
        webViewProvider.getInstance().apply {
            webViewClient = Client()
            webChromeClient = InnerWebChromeClient()
            settings.mixedContentMode = 0
            settings.userAgentString = settings.userAgentString.replace("; wv", "")

            val cockieMan = CookieManager.getInstance()
            val cockieManVal = getValForSetting(null)

            cockieMan.setAcceptThirdPartyCookies(this, cockieManVal)
            cockieMan.setAcceptCookie(cockieManVal)

            settings.cacheMode = 1 * -1
        }
        this.addContentView(webViewProvider.getInstance(), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        webViewProvider.getInstance().loadUrl(mainUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setValueForSettings() {
        webViewProvider.getInstance().settings.apply {
            domStorageEnabled = getValForSetting(null)
            allowContentAccess = getValForSetting(null)
            databaseEnabled = getValForSetting(null)
            loadWithOverviewMode = getValForSetting(null)
            useWideViewPort = getValForSetting(null)
            allowFileAccess = getValForSetting(null)
            javaScriptCanOpenWindowsAutomatically = getValForSetting(null)
            javaScriptEnabled = getValForSetting(null)
            allowFileAccessFromFileURLs = getValForSetting(null)
            allowUniversalAccessFromFileURLs = getValForSetting(null)
        }
    }

    private fun getValForSetting(whichValue: Int? = 0): Boolean {
        return whichValue == null
    }

    class Client : WebViewClient() {
        private var view: WebView? = null
        private var account: String? = null
        private var realm: String = ""
        private var args: String = ""

        private fun printReceivedLoginRequest() {
            Log.i("WebViewClient", "View: $view.\nAccount: $account.\nRealm: $realm.\nArgs: $args.")
        }

        override fun onReceivedLoginRequest(
            view: WebView,
            realm: String,
            account: String?,
            args: String
        ) {
            this.account = account
            this.view = view
            this.realm = realm
            this.args = args

            printReceivedLoginRequest()
            super.onReceivedLoginRequest(view, realm, account, args)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
            !(request.url.toString().contains("/") && request.url.toString().contains("http"))

    }

    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean? ->
        lifecycleScope.launch(Dispatchers.IO) {
            val intentForPass = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                val (fileString, formatString) = getStringBuilders()
                createTemp(fileString, formatString).also {
                    callback = Uri.fromFile(it)
                    intentForPass.putExtra(
                        "output",
                        Uri.fromFile(it)
                    )
                }
            }
            catch (_: Exception) {}
            workWithPrevious(arrayOf(intentForPass))
        }
    }

    private fun getStringBuilders(): Pair<StringBuilder, StringBuilder> {
        val fileString = StringBuilder()
        for(c in "temp_file") {
            fileString.append(c)
        }
        val formatString = StringBuilder()
        for(c in ".png") {
            formatString.append(c)
        }
        return fileString to formatString
    }

    private fun createTemp(fileString: StringBuilder, formatString: StringBuilder): File {
        return File.createTempFile(
            fileString.toString(),
            formatString.toString(),
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    private fun workWithPrevious(receivedIntent: Array<Intent>) {
        val startNewIntent = { previous: Intent ->
            Intent(Intent.ACTION_CHOOSER).also { ci ->
                ci.putExtra(Intent.EXTRA_INITIAL_INTENTS, receivedIntent)
                ci.putExtra(Intent.EXTRA_INTENT, previous)
                startActivityForResult(ci, 1)
            }
        }
        val previous = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        previous.type = getTypeStringBuilder().toString()
        startNewIntent(previous)
    }
    private fun getTypeStringBuilder() = StringBuilder("*/*")
    private fun String.toUri(): Uri = Uri.parse(this@toUri)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImportantCallback.callback?.onReceiveValue(if (resultCode != -1) {
            null
        } else {
            checkData(data) ?: callback?.toArray()
        })
        ImportantCallback.callback = null
    }

    private fun checkData(data: Intent?): Array<Uri>? {
        if(data != null) {
            if(data.dataString != null) {
                if(data.dataString?.toUri() != null) {
                    return data.dataString?.toUri()?.toArray()
                }
            }
        }
        return null
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webViewProvider.getInstance().saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webViewProvider.getInstance().restoreState(savedInstanceState)
    }

    inner class InnerWebChromeClient: WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            requestPermissionLauncher.launch("android.permission.CAMERA")
            ImportantCallback.callback = filePathCallback
            return true
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WebWindow::class.java)
        }
    }
}