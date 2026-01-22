package app.x1co.newwebclaude

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import app.x1co.newwebclaude.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val tabManager = TabManager()
    private var isFullscreen = false
    private val historyManager = HistoryManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupWebView()
        setupButtons()
        setupBackPress()

        loadHomePage()
    }

    private fun setupUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                userAgentString = buildUserAgent()
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false

                    if (WebShield.isBlocked(url)) {
                        showBlockedPage(url)
                        return true
                    }

                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        if (!WebShield.isBlocked(it)) {
                            binding.urlBar.setText(it)
                            tabManager.updateCurrentTab(it, view?.title ?: it)
                            historyManager.addToHistory(it, view?.title ?: it)
                        }
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    enterFullscreen()
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    exitFullscreen()
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                    binding.progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnGo.setOnClickListener {
            val url = binding.urlBar.text.toString()
            loadUrl(url)
        }

        binding.btnBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            }
        }

        binding.btnForward.setOnClickListener {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
            }
        }

        binding.btnRefresh.setOnClickListener {
            binding.webView.reload()
        }

        binding.btnHome.setOnClickListener {
            loadHomePage()
        }

        binding.btnTabs.setOnClickListener {
            showTabsDialog()
        }

        binding.btnHistory.setOnClickListener {
            showHistoryDialog()
        }

        binding.btnPip.setOnClickListener {
            enterPipMode()
        }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun buildUserAgent(): String {
        val webViewVersion = getWebViewVersion()
        val androidVersion = Build.VERSION.RELEASE
        val device = "${Build.MANUFACTURER} ${Build.MODEL}"

        return "Mozilla/5.0 Chrome/$webViewVersion NewWeb/Sky based on WebView $webViewVersion on Android $androidVersion Safari/537.36 AppleWebKit/537.36 running on $device"
    }

    private fun getWebViewVersion(): String {
        return try {
            val info = packageManager.getPackageInfo("com.google.android.webview", 0)
            info.versionName.split(".").take(3).joinToString(".")
        } catch (e: PackageManager.NameNotFoundException) {
            "120.0.0"
        }
    }

    private fun loadUrl(input: String) {
        var url = input.trim()

        if (WebShield.isBlocked(url)) {
            showBlockedPage(url)
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = if (url.contains(".") && !url.contains(" ")) {
                "https://$url"
            } else {
                "https://www.google.com/search?q=${url.replace(" ", "+")}"
            }
        }

        binding.webView.loadUrl(url)
    }

    private fun loadHomePage() {
        binding.urlBar.setText("")
        binding.webView.loadUrl("https://www.google.com")
    }

    private fun showBlockedPage(url: String) {
        val blockedHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        background: #000;
                        color: #fff;
                        font-family: -apple-system, system-ui, sans-serif;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        padding: 20px;
                        text-align: center;
                    }
                    .icon {
                        width: 120px;
                        height: 120px;
                        background: #ff3b30;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-bottom: 30px;
                        font-size: 80px;
                        font-weight: bold;
                    }
                    h1 {
                        font-size: 24px;
                        margin-bottom: 10px;
                        color: #fff;
                    }
                    p {
                        color: #999;
                        margin-bottom: 30px;
                        font-size: 16px;
                    }
                    button {
                        background: #007aff;
                        color: white;
                        border: none;
                        padding: 16px 32px;
                        border-radius: 12px;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: opacity 0.2s;
                    }
                    button:active {
                        opacity: 0.7;
                    }
                </style>
            </head>
            <body>
                <div class="icon">✕</div>
                <h1>This Website is blocked by WebShield</h1>
                <p>Este site foi bloqueado para sua proteção</p>
                <button onclick="window.location.href='https://www.google.com'">Go back to Home Page</button>
            </body>
            </html>
        """.trimIndent()

        binding.webView.loadDataWithBaseURL(null, blockedHtml, "text/html", "UTF-8", null)
    }

    private fun enterFullscreen() {
        isFullscreen = true
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.toolbar.visibility = View.GONE
        binding.bottomNav.visibility = View.GONE
    }

    private fun exitFullscreen() {
        isFullscreen = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        binding.toolbar.visibility = View.VISIBLE
        binding.bottomNav.visibility = View.VISIBLE
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        } else {
            Toast.makeText(this, "PIP não disponível nesta versão", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            binding.toolbar.visibility = View.GONE
            binding.bottomNav.visibility = View.GONE
        } else {
            if (!isFullscreen) {
                binding.toolbar.visibility = View.VISIBLE
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }

    private fun showTabsDialog() {
        val adapter = TabsAdapter(tabManager.getTabs()) { tab ->
            binding.webView.loadUrl(tab.url)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Abas (${tabManager.getTabs().size})")
            .setView(androidx.recyclerview.widget.RecyclerView(this).apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
            })
            .setPositiveButton("Nova Aba") { _, _ ->
                tabManager.addTab("https://www.google.com", "Nova Aba")
                loadHomePage()
            }
            .setNegativeButton("Fechar", null)
            .create()

        dialog.show()
    }

    private fun showHistoryDialog() {
        val history = historyManager.getHistory()
        val items = history.map { it.title }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Histórico")
            .setItems(items) { _, which ->
                binding.webView.loadUrl(history[which].url)
            }
            .setPositiveButton("Limpar") { _, _ ->
                historyManager.clearHistory()
                Toast.makeText(this, "Histórico limpo", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Fechar", null)
            .show()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }
}